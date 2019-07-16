import * as  groupDb from '../database/group';
import * as voteDb from '../database/votes';
import * as matchDb from '../database/matches';
import { CallableContext } from 'firebase-functions/lib/providers/https';
import { DetailedGroup, IGroup } from '../models/group';
import { IMatchDetails } from '../models/match';

describe('Get Group By Id', () => {
  it('throws an error if unauthorized', (done) => {
    const {getGroupByIdImpl} = require('./get-group-by-id');
    getGroupByIdImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  xit('successfully gets the group', (done) => {
    jest.spyOn(groupDb, 'getGroup').mockReturnValueOnce(Promise.resolve({
      matches: {
        m1: true,
      },
      members: {
        u1: true,
      },
      query: 'lunch',
      description: 'Lunch',
      name: 'Saturday Lunch',
      type: 'restaurant',
      numberResults: 5,
      location: {
        latitude: 10,
        longitude: 20,
      },
      voteConclusion: 100,
      daysToExpire: 5,
    }));
    jest.spyOn(voteDb, 'getGroupVotes').mockReturnValueOnce(Promise.resolve({
      u1: {
        m1: true,
      },
    }));
    jest.spyOn(matchDb, 'getMatches').mockReturnValueOnce(Promise.resolve([{
      fetchTime: 0,
      details: {
        id: 'm1',
        name: 'Match 1',
      },
    }]));
    const {getGroupByIdImpl} = require('./get-group-by-id');

    getGroupByIdImpl({
      groupId: 'g1'
    }, {
      auth: {uid: 'u1'},
    } as CallableContext).then((result) => {
      const res = new DetailedGroup({
        name: 'Saturday Lunch',
        type: 'restaurant',
        query: 'lunch',
        description: 'Lunch',
        location: {
          latitude: 10,
          longitude: 20,
        },
        numberResults: 5,
        voteConclusion: 100,
        daysToExpire: 5,
      } as IGroup);
      res.setVoteState({
        u1: {
          m1: true,
        },
      });
      res.setMatches([{
        fetchTime: 0,
        details: {
          id: 'm1',
          name: 'Match 1',
        } as IMatchDetails,
      }]);
      expect(result).toEqual({
        results: res,
      });
      done();
    });
  })
});