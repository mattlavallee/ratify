import { getAllGroups, cleanGroups } from '../database/group';
import { IGroup } from '../models/group';
import { getAllUsers, cleanUsers } from '../database/user';
import { User } from '../models/user';
import { getAllMatches, cleanMatches } from '../database/matches';
import { getAllVotes, cleanVotes } from '../database/votes';

function updateUsers(allUsers: {[key: string]: User}, groupId: string) {
  const userIds = Object.keys(allUsers);
  userIds.forEach((userId: string): void => {
    const user = allUsers[userId];
    if (user.created_groups && user.created_groups[groupId]) {
      delete user.created_groups[groupId];
    }
    if (user.joined_groups && user.joined_groups[groupId]) {
      delete user.joined_groups[groupId];
    }
  });
}

function updateMatches(allMatches: {[key: string]: any}, groupId: string) {
  const matchIds = Object.keys(allMatches);
  matchIds.forEach((matchId: string): void => {
    if(matchId.indexOf(groupId) === 0) {
      delete allMatches[matchId];
    }
  });
}

function updateVotes(allVotes: {[key: string]: any}, groupId: string) {
  const voteIds = Object.keys(allVotes);
  voteIds.forEach((voteId: string): void => {
    if(voteId.indexOf(groupId) === 0) {
      delete allVotes[voteId];
    }
  });
}

export function cleanImpl(res:any, resp: any): Promise<any> {
  const userPromise = getAllUsers();
  const groupPromise = getAllGroups();
  const matchPromise = getAllMatches();
  const votePromise = getAllVotes();
  return Promise.all([
    userPromise,
    groupPromise,
    matchPromise,
    votePromise,
  ]).then((results: any[]) => {
    const allUsers = results[0];
    const allGroups = results[1] as {[key: string]: IGroup};
    const allMatches = results[2];
    const allVotes = results[3];

    const groupIds = Object.keys(allGroups);
    const today = new Date();
    groupIds.forEach((groupId: string) => {
      const finalExpiration = new Date(allGroups[groupId].voteConclusion);
      finalExpiration.setDate(finalExpiration.getDate() + allGroups[groupId].daysToExpire);

      if (finalExpiration < today) {
        updateUsers(allUsers, groupId);
        updateMatches(allMatches, groupId);
        updateVotes(allVotes, groupId);
        delete allGroups[groupId];
      }
    });

    return Promise.all([
      cleanUsers(allUsers),
      cleanMatches(allMatches),
      cleanVotes(allVotes),
      cleanGroups(allGroups),
    ]).then(() => {
      resp.status(200).send('OK');
    }).catch(() => {
      resp.status(500).send('FAIL');
    });
  });
}
