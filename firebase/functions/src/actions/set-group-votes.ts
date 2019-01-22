import { CallableContext } from 'firebase-functions/lib/providers/https';
import { setUserVotes } from '../database/votes';

export function setGroupVotesImpl(data: any, context: CallableContext): Promise<any> {
  const userIsValidated = context.auth.uid && context.auth.uid.length > 0;
  if (!userIsValidated) {
    return Promise.resolve({error: 'Error authenticating the user'});
  }

  const userId = context.auth.uid;
  const groupId = data.groupId;
  const votes: {[key: string]: any}[] = data.votes
  if (!groupId || !votes || !votes.length) {
    return Promise.resolve({error: 'Invalid parameters to update user votes'});
  }

  const sanitizedUserVotes = votes.map((vote: any) => {
    return {
      id: groupId + '|' + vote.matchId,
      value: vote.value,
    };
  });

  return setUserVotes(userId, sanitizedUserVotes).then((result: boolean) => {
    if (result) {
      return true;
    }

    throw new Error('Error updating user votes');
  });
}
