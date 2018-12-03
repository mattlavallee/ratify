import * as yelpRequester from '../utilities/yelp-requester';
import * as  groupDb from '../database/group';
import * as userDb from '../database/user';
import * as voteDb from '../database/votes';
import * as matchDb from '../database/matches';
import { CallableContext } from 'firebase-functions/lib/providers/https';
import { User } from '../models/user';
import { IGroup } from '../models/group';
import { YelpResult, IYelpFullResult } from '../models/yelp-result';
import * as uuidUtil from '../utilities/uuid';

describe('Create Group', () => {
  it('throws an error if unauthorized', (done) => {
    const {createGroupImpl} = require('./create-group');
    createGroupImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  it('throws an error if the group is invalid', (done) => {
    const {createGroupImpl} = require('./create-group');
    createGroupImpl({
      name: 'foo',
      type: 'blah',
      description: 'something',
      activity: 'bar',
      startingLocation: 'somewhere',
      latitude: 'blah',
      longitude: 73,
      results: 300,
      conclusion: (new Date()).getTime(),
      expiration: 5,
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Invalid group!');
      done();
    });
  });

  it('returns an error if the group already exists', (done) => {
    jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([]));
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({name: 'foo'} as IGroup));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('foo')));
    const {createGroupImpl} = require('./create-group');

    createGroupImpl({
      name: 'foo',
      type: 'blah',
      description: 'something',
      activity: 'bar',
      startingLocation: 'somewhere',
      latitude: 68,
      longitude: 73,
      results: 3,
      conclusion: (new Date()).getTime(),
      expiration: 5,
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Oh no! There was an error creating your group!');
      done();
    });
  });

  it('returns an error if there was a problem getting user info', (done) => {
    jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([]));
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(null));
    const {createGroupImpl} = require('./create-group');

    createGroupImpl({
      name: 'foo',
      type: 'blah',
      description: 'something',
      activity: 'bar',
      startingLocation: 'somewhere',
      latitude: 68,
      longitude: 73,
      results: 3,
      conclusion: (new Date()).getTime(),
      expiration: 5,
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error getting your user info!');
      done();
    });
  });

  it('successfully creates a group', (done) => {
    jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([
      new YelpResult({
        id: 'match1',
        name: 'A Match',
        image_url: 'match.png',
        rating: 4.3,
        price: '$$',
        location: {
          display_address: ['ABC 123 St']
        }} as IYelpFullResult),
    ]));
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
    jest.spyOn(groupDb, 'insertGroup').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('foo')));
    jest.spyOn(userDb, 'updateUser').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(matchDb, 'insertMatches').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(voteDb, 'insertUserVotes').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(uuidUtil, 'generateUuid').mockReturnValueOnce('12345');
    const {createGroupImpl} = require('./create-group');

    createGroupImpl({
      name: 'foo',
      type: 'blah',
      description: 'something',
      activity: 'bar',
      startingLocation: 'somewhere',
      latitude: 68,
      longitude: 73,
      results: 3,
      conclusion: (new Date()).getTime(),
      expiration: 5,
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(result).toEqual({groupId: '12345'});
      done();
    });
  });

  it('successfully creates a group if matches were provided via preview', (done) => {
    jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve());
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
    jest.spyOn(groupDb, 'insertGroup').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('foo')));
    jest.spyOn(userDb, 'updateUser').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(matchDb, 'insertMatches').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(voteDb, 'insertUserVotes').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(uuidUtil, 'generateUuid').mockReturnValueOnce('12345');
    const {createGroupImpl} = require('./create-group');

    createGroupImpl({
      name: 'foo',
      type: 'blah',
      description: 'something',
      activity: 'bar',
      startingLocation: 'somewhere',
      latitude: 68,
      longitude: 73,
      results: 3,
      conclusion: (new Date()).getTime(),
      expiration: 5,
      matches: [
        new YelpResult({
          id: 'match1',
          name: 'A Match',
          image_url: 'match.png',
          rating: 4.3,
          price: '$$',
          location: {
            display_address: ['ABC 123 St']
          }} as IYelpFullResult),
      ],
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(yelpRequester.getYelpResultsForGroup).not.toHaveBeenCalled();
      expect(result).toEqual({groupId: '12345'});
      done();
    });
  });
});