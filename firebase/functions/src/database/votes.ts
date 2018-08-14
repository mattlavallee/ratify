import { getDatabase } from './db-instance';
import { database } from '../../node_modules/firebase-admin';
import { IVote } from '../models/interfaces';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';

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

export function insertUserVotes(votes: IVote): Promise<boolean> {
  return getVoteDBReference().limitToFirst(1).once('value').catch(() => false).then((snapshot: DataSnapshot) => {
    const promises: Array<Promise<void>> = [];
    if (snapshot.val()) {
      for (const key in votes) {
        if (votes.hasOwnProperty(key)) {
          promises.push(getVoteDBReference().child(key).set(votes[key]));
        }
      }
    } else {
      promises.push(getVoteDBReference().set(votes));
    }

    return Promise.all(promises).then(() => true).catch(() => false);
  });
}