import { getDatabase } from './db-instance';
import { User } from '../models/user';
import { errorCodes } from '../utilities/firebase-error-codes';
import { IUserGroup, IGroup } from '../models/group';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';
import { HttpsError } from '../../node_modules/firebase-functions/lib/providers/https';
import { database } from '../../node_modules/firebase-admin';

let groupReference: database.Reference;
function getGroupDBReference() {
  if (!groupReference) {
    groupReference = getDatabase().ref('groups/');
  }
  return groupReference;
}

function isGroupExpired(group: IGroup): boolean {
  const expirationDate = new Date(group.voteConclusion);
  expirationDate.setDate(expirationDate.getDate() + (group.daysToExpire));
  return expirationDate < new Date();
}

export function getAllGroups(): Promise<{[key:string]: IGroup}> {
  return getGroupDBReference().once('value').catch((err: Error) => {
    return new HttpsError((errorCodes as any).internal, err.message);
  }).then((snapshot: DataSnapshot): any => {
    return snapshot.val() as {[key: string]: IGroup};
  });
}

export function cleanGroups(allGroups: any): Promise<boolean> {
  return getGroupDBReference().set(allGroups).then(() => true);
}

export function getGroup(groupId: string): Promise<IGroup> {
  return getGroupDBReference().child(groupId).once('value').catch((err: Error) => {
    return new HttpsError((<any>errorCodes).internal, err.message);
  }).then((groupSnapshot: DataSnapshot): IGroup => {
    return groupSnapshot.val() as IGroup;
  });
}

export function getGroupsForUser(userModel: User): Promise<IUserGroup> {
  const createdGroupKeys = Object.keys(userModel.created_groups || {});
  const joinedGroupKeys = Object.keys(userModel.joined_groups || {});
  const createdGroupPromises: Promise<database.DataSnapshot>[] = [];
  const joinedGroupPromises: Promise<database.DataSnapshot>[] = [];
  for (const cKey of createdGroupKeys) {
    createdGroupPromises.push(getGroupDBReference().child(cKey).once('value'));
  }
  for (const jKey of joinedGroupKeys) {
    joinedGroupPromises.push(getGroupDBReference().child(jKey).once('value'));
  }

  const result: IUserGroup = {
    created_groups: {},
    joined_groups: {},
  };
  const resolvedCreatedPromise = Promise.all(createdGroupPromises).then((groups: DataSnapshot[]) => {
    for (let i = 0; i < createdGroupKeys.length; i++) {
      const currGroup = groups[i].val();
      if (!isGroupExpired(currGroup)) {
        result.created_groups[createdGroupKeys[i]] = currGroup;
      }
    }
  });
  const resolvedJoinedPromise = Promise.all(joinedGroupPromises).then((groups: DataSnapshot[]) => {
    for (let i = 0; i < joinedGroupKeys.length; i++) {
      const currGroup = groups[i].val();
      if (!isGroupExpired(currGroup)) {
        result.joined_groups[joinedGroupKeys[i]] = currGroup;
      }
    }
  });

  return Promise.all([resolvedCreatedPromise, resolvedJoinedPromise]).then(() => result);
}

export function insertGroup(groupId: string, model: IGroup): Promise<boolean> {
  return getGroupDBReference().limitToFirst(1).once('value').then((snapshot: DataSnapshot): Promise<boolean> => {
    if (snapshot.val()) {
      return getGroupDBReference().child(groupId).set(model).then(() => true).catch(() => false);
    }

    const newGroupEntry: {[key:string]: IGroup} = {
      [groupId]: model,
    };
    return getGroupDBReference().set(newGroupEntry).then(() => true).catch(() => false);
  }).catch(() => false);
}

export function updateGroup(groupId: string, model: IGroup): Promise<boolean> {
  return getGroupDBReference().child(groupId).set(model).then(() => true).catch(() => false);
}