import * as dbInstance from './db-instance';

let limitOnceThrowsError = false;
let groupExists = true;
let onceThrowsError = false;
let handleExpiration = false;
let count = -1;
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
                return groupExists ? {} : null;
              },
            });
          }
        };
      },
      set: () => Promise.resolve(),
      child: (groupId: any) => {
        if (typeof groupId !== 'string') {
          throw new Error('Invalid group id');
        }
        return {
          set: (model: any) => {
            if (!model) {
              return Promise.reject(new Error('no model provided!'));
            }
            return Promise.resolve();
          },
          once: () => {
            if (onceThrowsError) {
              return Promise.reject(new Error('group invalid'));
            }
            return Promise.resolve({
              val: () => {
                if (!handleExpiration) {
                  return {};
                }

                count++;
                if (count % 2 === 0) {
                  const date = new Date();
                  date.setDate(date.getDate() - 2);
                  return {
                    voteConclusion: date.getTime(),
                    daysToExpire: 1,
                  };
                } else {
                  const expiredDate = new Date();
                  expiredDate.setDate(expiredDate.getDate() + 5);
                  return {
                    voteConclusion: expiredDate.getTime(),
                    daysToExpire: 1,
                  };
                }
              },
            });
          },
        };
      }
    };
  },
};

describe('Group Database Handler', () => {
  describe('getGroup - ', () => {
    it('returns the specified group', (done) => {
      onceThrowsError = false;
      handleExpiration = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getGroup} = require('./group');

      getGroup('g1').then((response) => {
        expect(response).toEqual({});
        done();
      });
    });

    it('handles errors properly', (done) => {
      onceThrowsError = true;
      handleExpiration = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getGroup} = require('./group');

      getGroup('g1').catch(() => {
        expect(true).toEqual(true);
        done();
      });
    });
  });

  describe('getGroupsForUser - ', () => {
    it('properly gets groups for the given user', (done) => {
      onceThrowsError = false;
      handleExpiration = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getGroupsForUser} = require('./group');

      getGroupsForUser({
        created_groups: {
          g1: true,
          g4: true,
        },
        joined_groups: {
          g2: true,
          g3: true,
        },
      }).then((response) => {
        expect(response.created_groups.g1).not.toBeDefined();
        expect(response.created_groups.g4).toBeDefined();
        expect(response.joined_groups.g2).not.toBeDefined();
        expect(response.joined_groups.g3).toBeDefined();
        done();
      });
    });
  });

  describe('insertGroup - ', () => {
    it('inserts a group if the group reference already exists', (done) => {
      groupExists = true;
      limitOnceThrowsError = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertGroup} = require('./group');

      insertGroup('g1', {}).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });

    it('inserts a group if the group reference does not exist yet', (done) => {
      groupExists = false;
      limitOnceThrowsError = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertGroup} = require('./group');

      insertGroup('g1', {}).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });

    it('handles errors properly', (done) => {
      groupExists = true;
      limitOnceThrowsError = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertGroup} = require('./group');

      insertGroup('g1', {}).then((response) => {
        expect(response).toEqual(false);
        done();
      });
    });

    it('handles no model being provided properly', (done) => {
      groupExists = true;
      limitOnceThrowsError = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertGroup} = require('./group');

      insertGroup('g1', null).then((response) => {
        expect(response).toEqual(false);
        done();
      });
    });
  });

  describe('updateGroup - ', () => {
    it('updates a group', (done) => {
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {updateGroup} = require('./group');

      updateGroup('g1', {}).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });

    it('throws an error if the group model is not provided', (done) => {
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {updateGroup} = require('./group');

      updateGroup('g1', null).then((response) => {
        expect(response).toEqual(false);
        done();
      });
    });
  });
});