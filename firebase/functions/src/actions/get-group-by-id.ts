import { CallableContext } from 'firebase-functions/lib/providers/https';
import { getGroup } from '../database/group';
import { IGroup, DetailedGroup } from '../models/group';
import { getGroupVotes } from '../database/votes';
import { getMatches } from '../database/matches';

export function getGroupByIdImpl(data: any, context: CallableContext): Promise<any> {
  const userIsValidated = context.auth.uid && context.auth.uid.length > 0;
  if (!userIsValidated) {
    return Promise.resolve({error: 'Error authenticating the user'});
  }

  return getGroup(data.groupId).then((requestedGroup: IGroup) => {
    const group = new DetailedGroup(requestedGroup);

    const matchIds = Object.keys(requestedGroup.matches);
    const groupMatchKeys = matchIds.map((id: string) => data.groupId + '|' + id);

    const votePromise = getGroupVotes(groupMatchKeys);
    const matchPromise = getMatches(groupMatchKeys);
    return Promise.all([votePromise, matchPromise]).then((results) => {
      group.setVoteState(results[0]);
      group.setMatches(results[1]);

      return {
        results: group,
      };
    });
  });
}