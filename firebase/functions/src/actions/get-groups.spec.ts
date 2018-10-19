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
});