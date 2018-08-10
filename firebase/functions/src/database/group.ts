import {getDatabase} from './db-instance';
import { User } from '../models/user';
import { IUserGroup } from '../models/group';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';
import { database } from '../../node_modules/firebase-admin';

let groupReference: database.Reference;
function getGroupDBReference() {
  if (!groupReference) {
    groupReference = getDatabase().ref('groups/');
  }
  return groupReference;
}

export function getGroupsForUser(userModel: User): Promise<IUserGroup> {
  const createdGroupKeys = Object.keys(userModel.created_groups || {});
  const joinedGroupKeys = Object.keys(userModel.joined_groups || {});
  const createdGroupPromises: Promise<DataSnapshot>[] = [];
  const joinedGroupPromises: Promise<DataSnapshot>[] = [];
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
      result.created_groups[createdGroupKeys[i]] = groups[i].val();
    }
  });
  const resolvedJoinedPromise = Promise.all(joinedGroupPromises).then((groups: DataSnapshot[]) => {
    for (let i = 0; i < joinedGroupKeys.length; i++) {
      result.joined_groups[joinedGroupKeys[i]] = groups[i].val();
    }
  });

  return Promise.all([resolvedCreatedPromise, resolvedJoinedPromise]).then(() => result);
}