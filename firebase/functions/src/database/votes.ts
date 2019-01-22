import { getDatabase } from './db-instance';
import { database } from '../../node_modules/firebase-admin';
import { IVote, IDetailedGroupVotes } from '../models/vote';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';

let voteReference: database.Reference;
function getVoteDBReference() {
  if (!voteReference) {
    voteReference = getDatabase().ref('match_votes/');
  }
  return voteReference;
}

export function setUserVotes(userId: string, votes: Array<{[key: string]: string|number}>): Promise<boolean> {
  const promiseArr = [];
  for (const currVote of votes) {
    const promise: Promise<void> = getVoteDBReference().child(currVote.id as string).once('value').then((vote: DataSnapshot) => {
      const instance = vote.val();
      if (instance) {
        instance[userId] = currVote.value === 0 ? false : currVote.value;
        return getVoteDBReference().child(currVote.id as string).set(instance);
      }

      return Promise.reject('error updating votes: ' + currVote.id + ' - ' + userId);
    });
    promiseArr.push(promise);
  }

  return Promise.all(promiseArr).then(() => true).catch(() => false);
}

export function initUserVotes(userId: string, voteIds: Array<string>): Promise<boolean> {
  const promiseArr = [];
  for (const voteId of voteIds) {
    const prom: Promise<void> = getVoteDBReference().child(voteId).once('value').then((vote: DataSnapshot) => {
      const instance = vote.val();
      if (instance) {
        instance[userId] = false;
        return getVoteDBReference().child(voteId).set(instance);
      }

      return Promise.reject('error updating votes: ' + voteId + ' - ' + userId);
    });
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

export function getGroupVotes(ids: string[]): Promise<any> {
  const votePromises = [];
  for (const voteId of ids) {
    const prom: Promise<void> = getVoteDBReference().child(voteId).once('value').then((vote: DataSnapshot) => {
      return vote.val();
    });
    votePromises.push(prom);
  }
  
  return Promise.all(votePromises).then((matchVotes: {[userId: string]: boolean}[]) => {
    const userVotesForMatches: IDetailedGroupVotes = {};
    for (let i = 0; i < matchVotes.length; i++) {
      const currMatchVote = matchVotes[i];
      if (!currMatchVote) {
        continue;
      }

      const matchId = ids[i].split('|')[1];
      const userIds = Object.keys(currMatchVote);
      userIds.forEach((userId: string) => {
        if (!userVotesForMatches[userId]) {
          userVotesForMatches[userId] = {};
        }

        userVotesForMatches[userId][matchId] = currMatchVote[userId];
      });
    }
    return userVotesForMatches;
  });
}