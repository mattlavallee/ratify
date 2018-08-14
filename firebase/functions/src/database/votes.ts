import { getDatabase } from './db-instance';
import { database } from '../../node_modules/firebase-admin';

let voteReference: database.Reference;
function getVoteDBReference() {
  if (!voteReference) {
    voteReference = getDatabase().ref('match_votes/');
  }
  return voteReference;
}

export function initUserVotes(userId: string, voteIds: Array<string>): Promise<boolean> {
  const promiseArr = [];
  for (const voteId of voteIds) {
    const prom: Promise<void> = getVoteDBReference().child(voteId).set({[userId]: false});
    promiseArr.push(prom);
  }

  return Promise.all(promiseArr).then(() => true).catch(() => false);
}