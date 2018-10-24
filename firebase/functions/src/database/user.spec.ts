import * as dbInstance from './db-instance';

let onceThrowsError = false;
let limitOnceThrowsError = false;
let userExists = true;
const dbMock = {
  ref: () => {
    return {
      limitToFirst: () => {
        return {
          once: () => {
            if (limitOnceThrowsError) {
              return Promise.reject('error');
            }
            return Promise.resolve({
              val: () => {
                return userExists ? {} : null;
              },
            });
          }
        };
      },
      set: () => Promise.resolve(),
      child: (userId: any) => {
        if (typeof userId !== 'string') {
          throw new Error('Invalid user id');
        }
        return {
          set: (model: any) => {
            if (!model) {
              throw new Error('no model provided!');
            }
            return Promise.resolve();
          },
          once: () => {
            if (onceThrowsError) {
              return Promise.reject(new Error('user invalid'));
            }
            return Promise.resolve({
              val: () => {
                return {};
              },
            });
          },
        };
      }
    };
  },
};

describe('User Database Handler', () => {
  describe('updateUser - ', () => {
    it('throws no error', (done) => {
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {updateUser} = require('./user');

      updateUser('foo', {}).then(() => {
        expect(true).toEqual(true);
        done();
      });
    });
  });

  describe('getUser - ', () => {
    it('throws an error if user is not found', (done) => {
      onceThrowsError = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getUser} = require('./user');

      getUser('foo').catch(() => {
        expect(true).toEqual(true);
        done();
      });
    });

    it ('returns the user', (done) => {
      onceThrowsError = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getUser} = require('./user');

      getUser('foo').then((response) => {
        expect(response).toEqual({});
        done();
      });
    });
  });

  describe('createUser - ', () => {
    it('throws an error if db encounters an error', (done) => {
      limitOnceThrowsError = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {createUser} = require('./user');

      createUser('foo', {}).catch(() => {
        expect(true).toEqual(true);
        done();
      });
    });

    it('inserts a user when the db section already exists', (done) => {
      limitOnceThrowsError = false;
      userExists = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {createUser} = require('./user');

      createUser('foo', {}).then((response) => {
        expect(response).toEqual({
          created_groups: {},
          joined_groups: {},
        });
        done();
      });
    });

    it('inserts a user when the db section does not exists', (done) => {
      limitOnceThrowsError = false;
      userExists = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {createUser} = require('./user');

      createUser('foo', {}).then((response) => {
        expect(response).toEqual({
          created_groups: {},
          joined_groups: {},
        });
        done();
      });
    });
  });
});