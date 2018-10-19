import * as  groupDb from '../database/group';
import * as userDb from '../database/user';
import { CallableContext } from 'firebase-functions/lib/providers/https';
import { User } from '../models/user';

describe('Get Groups', () => {
  it('throws an error if unauthorized', (done) => {
    const {getGroupsImpl} = require('./get-groups');
    getGroupsImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  it('gets groups for existing user', (done) => {
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test')));
    jest.spyOn(groupDb, 'getGroupsForUser').mockReturnValueOnce(Promise.resolve({
      created_groups: {a: {}},
      joined_groups: {b: {}, c:{}},
    }));
    const {getGroupsImpl} = require('./get-groups');

    getGroupsImpl({}, {auth: {uid: 'test'}} as CallableContext).then((result) => {
      expect(result).toEqual({
        created_groups: {a: {}},
        joined_groups: {b: {}, c: {}},
      });
      done();
    });
  });

  it('creates a new user if one does not exist', (done) => {
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(null));
    jest.spyOn(userDb, 'createUser').mockReturnValueOnce(Promise.resolve({
      created_groups: {},
      joined_groups: {},
    }));
    const {getGroupsImpl} = require('./get-groups');

    getGroupsImpl({}, {
      auth: {
        uid: 'test',
        token: {name:'Test'}
      }
    } as any).then((result) => {
      expect(result).toEqual({
        created_groups: {},
        joined_groups: {},
      });
      done();
    });
  });

  // it('returns an error if the group already exists', (done) => {
  //   jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([]));
  //   jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({name: 'foo'} as IGroup));
  //   jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('foo')));
  //   const {createGroupImpl} = require('./create-group');

  //   createGroupImpl({
  //     name: 'foo',
  //     type: 'blah',
  //     description: 'something',
  //     activity: 'bar',
  //     startingLocation: 'somewhere',
  //     latitude: 68,
  //     longitude: 73,
  //     results: 3,
  //     conclusion: (new Date()).getTime(),
  //     expiration: 5,
  //   }, {
  //     auth: {uid: 'test'}
  //   } as CallableContext).then((result) => {
  //     expect(result.error).toEqual('Oh no! There was an error creating your group!');
  //     done();
  //   });
  // });

  // it('returns an error if there was a problem getting user info', (done) => {
  //   jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([]));
  //   jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
  //   jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(null));
  //   const {createGroupImpl} = require('./create-group');

  //   createGroupImpl({
  //     name: 'foo',
  //     type: 'blah',
  //     description: 'something',
  //     activity: 'bar',
  //     startingLocation: 'somewhere',
  //     latitude: 68,
  //     longitude: 73,
  //     results: 3,
  //     conclusion: (new Date()).getTime(),
  //     expiration: 5,
  //   }, {
  //     auth: {uid: 'test'}
  //   } as CallableContext).then((result) => {
  //     expect(result.error).toEqual('Error getting your user info!');
  //     done();
  //   });
  // });

  // it('successfully creates a group', (done) => {
  //   jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([
  //     new YelpResult({
  //       id: 'match1',
  //       name: 'A Match',
  //       image_url: 'match.png',
  //       rating: 4.3,
  //       price: '$$',
  //       location: {
  //         display_address: ['ABC 123 St']
  //       }} as IYelpFullResult),
  //   ]));
  //   jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
  //   jest.spyOn(groupDb, 'insertGroup').mockReturnValueOnce(Promise.resolve(true));
  //   jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('foo')));
  //   jest.spyOn(userDb, 'updateUser').mockReturnValueOnce(Promise.resolve(true));
  //   jest.spyOn(matchDb, 'insertMatches').mockReturnValueOnce(Promise.resolve(true));
  //   jest.spyOn(voteDb, 'insertUserVotes').mockReturnValueOnce(Promise.resolve(true));
  //   jest.spyOn(uuidUtil, 'generateUuid').mockReturnValueOnce('12345');
  //   const {createGroupImpl} = require('./create-group');

  //   createGroupImpl({
  //     name: 'foo',
  //     type: 'blah',
  //     description: 'something',
  //     activity: 'bar',
  //     startingLocation: 'somewhere',
  //     latitude: 68,
  //     longitude: 73,
  //     results: 3,
  //     conclusion: (new Date()).getTime(),
  //     expiration: 5,
  //   }, {
  //     auth: {uid: 'test'}
  //   } as CallableContext).then((result) => {
  //     expect(result).toEqual({groupId: '12345'});
  //     done();
  //   });
  // });
});