import { getDatabase } from './db-instance';
import { database } from '../../node_modules/firebase-admin';
import { IMatch, ISingleMatch } from '../models/match';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';
import { YelpResult } from '../models/yelp-result';

let matchReference: database.Reference;
function getMatchDBReference() {
  if (!matchReference) {
    matchReference = getDatabase().ref('matches/');
  }
  return matchReference;
}

export function getAllMatches(): Promise<{[key: string]: ISingleMatch}> {
  return getMatchDBReference().once('value').catch(() => false).then((snapshot: DataSnapshot) => {
    return snapshot.val();
  });
}

export function cleanMatches(allMatches: any): Promise<boolean> {
  return getMatchDBReference().set(allMatches).then(() => true);
}

export function updateMatches(matches: YelpResult[], groupId: string): Promise<boolean> {
  return getAllMatches().then((allMatches: {[key: string]: ISingleMatch}) => {
    matches.forEach((match: YelpResult) => {
      if (match !== null && match !== undefined) {
        const key = groupId + '|' + match.id;
        allMatches[key].fetchTime = (new Date()).getTime();
        allMatches[key].details = match;
      }
    });

    return getMatchDBReference().set(allMatches).then(() => true);
  });
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

export function getMatches(ids: string[]): Promise<ISingleMatch[]> {
  const promises = [];
  ids.forEach((id: string) => {
    const prom = getMatchDBReference().child(id).once('value').then((value: DataSnapshot) => {
      return value.val();
    });
    promises.push(prom);
  });

  return Promise.all(promises).then((results: (ISingleMatch|undefined)[]) => {
    return results.filter((res) => res !== null && res !== undefined);
  });
}
