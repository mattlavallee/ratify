import { CallableContext } from 'firebase-functions/lib/providers/https';
import { getGroup } from '../database/group';
import { IGroup, DetailedGroup } from '../models/group';
import { getGroupVotes } from '../database/votes';
import { getMatches, updateMatches } from '../database/matches';
import { ISingleMatch } from '../models/match';
import { getYelpBusinessDetails } from '../utilities/yelp-requester';
import { YelpResult } from '../models/yelp-result';

function invokeUpdates(matches: ISingleMatch[], currIndex: number, promises: Promise<YelpResult>[], resolveFn: Function) {
  if (currIndex >= matches.length) {
    resolveFn(promises);
    return;
  }

  setTimeout(() => {
    promises.push(getYelpBusinessDetails(matches[currIndex].details.id));
    invokeUpdates(matches, currIndex + 1, promises, resolveFn);
  }, 250);
}

function getNewMatchPromises(matches: ISingleMatch[], currentDate: Date, groupId: string): Promise<boolean> {
  const matchesToUpdate = matches.filter((match: ISingleMatch) => {
    const fetchDate = new Date(match.fetchTime);
    fetchDate.setDate(fetchDate.getDate() + 1);
    return fetchDate < currentDate;
  });

  if (matchesToUpdate.length === 0) {
    return Promise.resolve(false);
  }

  const promises: Promise<YelpResult>[] = [];
  return new Promise(resolve => {
    invokeUpdates(matchesToUpdate, 0, promises, resolve);
  }).then((allPromises: Promise<YelpResult>[]) => {
    return Promise.all(allPromises).then((result: YelpResult[]) => {
      return updateMatches(result, groupId);
    });
  });
  
  // const promises: Promise<YelpResult>[] = [];
  // matches.filter((match: ISingleMatch) => {
  //   const fetchDate = new Date(match.fetchTime);
  //   fetchDate.setDate(fetchDate.getDate() + 1);
  //   return fetchDate < currentDate;
  // }).forEach((match: ISingleMatch) => {
  //   const prom = getYelpBusinessDetails(match.details.id);
  //   promises.push(prom);
  // });

  // if (promises.length === 0) {
  //   return Promise.resolve(false);
  // }

  // return Promise.all(promises).then((result: YelpResult[]) => {
  //   return updateMatches(result, groupId);
  // });
}

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
    const currentDate = new Date();
    return Promise.all([votePromise, matchPromise]).then((results) => {
      const matches = results[1];
      return getNewMatchPromises(matches, currentDate, data.groupId).then((needsNewFetch: boolean) => {
        if (!needsNewFetch) {
          return matchPromise;
        }
        return getMatches(groupMatchKeys);
      }).then((updatedMatches: ISingleMatch[]) => {
        group.setVoteState(results[0]);
        group.setMatches(updatedMatches);

        return {
          results: group,
        };
      });
    });
  });
}