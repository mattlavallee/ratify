import { getDatabase } from './db-instance';
import { database } from '../../node_modules/firebase-admin';
import { IMatch } from '../models/match';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';

let matchReference: database.Reference;
function getMatchDBReference() {
  if (!matchReference) {
    matchReference = getDatabase().ref('matches/');
  }
  return matchReference;
}

export function insertMatches(matches: IMatch): Promise<boolean> {
  return getMatchDBReference().limitToFirst(1).once('value').catch(() => false).then((snapshot: DataSnapshot) => {
    const promises: Array<Promise<void>> = [];
    if (snapshot.val()) {
      for (const key in matches) {
        if (matches.hasOwnProperty(key)) {
          promises.push(getMatchDBReference().child(key).set(matches[key]));
        }
      }
    } else {
      promises.push(getMatchDBReference().set(matches));
    }

    return Promise.all(promises).then(() => true).catch(() => false);
  });
}