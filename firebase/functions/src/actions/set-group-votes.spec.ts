import * as voteDb from '../database/votes';
import { CallableContext } from 'firebase-functions/lib/providers/https';

describe('Set Group Votes', () => {
  it('throws an error if unauthorized', (done) => {
    const {setGroupVotesImpl} = require('./set-group-votes');
    setGroupVotesImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  it ('successfully sets the group votes', (done) => {
    jest.spyOn(voteDb, 'setUserVotes').mockReturnValueOnce(Promise.resolve(true));
    const {setGroupVotesImpl} = require('./set-group-votes');
    setGroupVotesImpl({
      groupId: 'foo',
      votes: [{matchId: 'bar', value: 1}]}, {
      auth: {uid: 'foo'},
    } as CallableContext).then((result) => {
      expect(result).toEqual(true);
      done();
    });
  });
});