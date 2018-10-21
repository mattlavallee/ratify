import * as  groupDb from '../database/group';
import * as userDb from '../database/user';
import * as voteDb from '../database/votes';
import { CallableContext } from 'firebase-functions/lib/providers/https';
import { User } from '../models/user';

describe('Join Group', () => {
  it('throws an error if unauthorized', (done) => {
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  it('returns an error if the group does not exist', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve(null));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test')));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Aww, shucks, abc123 doesn\'t exist');
      done();
    });
  });

  it('returns an error if the user retrieval failed', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({}));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(null));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error retrieving user information');
      done();
    });
  });

  it('returns an error if the user created the group being joined', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({}));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test', {abc123:true})));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Hey! You\'ve already joined this group!');
      done();
    });
  });

  it('returns an error if the user already joined the group', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({}));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test', null, {abc123:true})));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Hey! You\'ve already joined this group!');
      done();
    });
  });
  
  it('returns an error if joining the group fails', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({
      members: {},
      matches: {a: true},
    }));
    jest.spyOn(groupDb, 'updateGroup').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test')));
    jest.spyOn(voteDb, 'initUserVotes').mockReturnValueOnce(Promise.resolve(null));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error joining the group!');
      done();
    });
  });

  it('successfully joins a group', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({
      members: {},
      matches: {a: true},
    }));
    jest.spyOn(groupDb, 'updateGroup').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(userDb, 'getUser').mockReturnValueOnce(Promise.resolve(new User('test')));
    jest.spyOn(userDb, 'updateUser').mockReturnValueOnce(Promise.resolve(true));
    jest.spyOn(voteDb, 'initUserVotes').mockReturnValueOnce(Promise.resolve({}));
    const {joinGroupImpl} = require('./join-group');

    joinGroupImpl({groupCode: 'abc123'}, {
      auth: {uid: 'test'},
    } as CallableContext).then((result) => {
      expect(result).toEqual({data: 'abc123'});
      done();
    });
  });
});