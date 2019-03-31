import * as dbInstance from './db-instance';

let childOnceHasInstance = true;
let limitOnceHasInstance = true;
let getVoteHasInstance = false;
const dbMock = {
  ref: () => {
    return {
      limitToFirst: () => {
        return {
          once: () => Promise.resolve({
            val: () => {
              return limitOnceHasInstance ? {} : null;
            },
          }),
        };
      },
      set: () => Promise.resolve(),
      update: () => Promise.resolve(),
      child: () => {
        return {
          set: () => Promise.resolve(),
          once: () => {
            if (getVoteHasInstance) {
              return Promise.resolve({val: () => { return {u1: true}}});
            }
            if (childOnceHasInstance) {
              return Promise.resolve({val: () => { return {}; }});
            }
            return Promise.resolve({val: () => null});
          },
        };
      }
    };
  },
};

describe('Votes Database Handler', () => {
  describe('initUserVotes - ', () => {
    it('properly resolves for all votes', (done) => {
      childOnceHasInstance = true;
      getVoteHasInstance = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {initUserVotes} = require('./votes');

      initUserVotes('foo', ['v1', 'v2']).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });
  });

  describe('setUserVotes', () => {
    it('throws an error if a vote returns undefined', (done) => {
      childOnceHasInstance = false;
      getVoteHasInstance = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {setUserVotes} = require('./votes');

      setUserVotes('foo', [{id: 'g1|v1', value: 1}]).then((response) => {
        expect(response).toEqual(false);
        done();
      });
    });

    it('saves user votes to the database', (done) => {
      childOnceHasInstance = true;
      getVoteHasInstance = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {setUserVotes} = require('./votes');

      setUserVotes('foo', [{id: 'g1|v1', value: 1}, {id: 'g1|v2', value: 0}]).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });
  });

  describe('insertUserVotes', () => {
    it('properly resolves for all votes', (done) => {
      limitOnceHasInstance = true;
      getVoteHasInstance = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertUserVotes} = require('./votes');

      insertUserVotes({
        v1: { u1: true },
        v2: { u1: true },
      }).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });

    it('updates votes if no instance found', (done) => {
      limitOnceHasInstance = false;
      getVoteHasInstance = false;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {insertUserVotes} = require('./votes');

      insertUserVotes({
        v1: { u1: true },
      }).then((response) => {
        expect(response).toEqual(true);
        done();
      });
    });
  });

  describe('getGroupVotes', () => {
    it('properly retrieves all votes', (done) => {
      childOnceHasInstance = false;
      getVoteHasInstance = true;
      jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
      const {getGroupVotes} = require('./votes');

      getGroupVotes(['g1|m1']).then((response) => {
        expect(response).toEqual({
          u1: {
            m1: true,
          },
        });
        done();
      })
    });
  });
});