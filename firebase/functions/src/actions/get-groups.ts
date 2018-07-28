import * as admin from 'firebase-admin'
import { HttpsError, CallableContext } from 'firebase-functions/lib/providers/https';

function createNewUser(userRef: admin.database.Reference, context: any): Promise<any> {
  const newUserObj: any = {};
  newUserObj[context.auth.uid] = {
    name: context.auth.token.name,
    created_groups: {},
    joined_groups: {},
  };
  return userRef.set(newUserObj).then(() => {
    return {
      created_groups: {},
      joined_groups: {},
    };
  });
}

function processResolvedGroups(keys: string[], groups: any[]): any {
  const allGroups: any = {};
  for (let i = 0; i < keys.length; i++) {
    allGroups[keys[i]] = groups[i].val();
  }
  return allGroups;
}

function getGroupsForUser(userObj: any, userUuid: string): Promise<any> {
  const createdGroupKeys = Object.keys(userObj.created_groups || {});
  const joinedGroupKeys = Object.keys(userObj.joined_groups || {});

  const db = admin.database();
  const createdGroupPromises: Promise<any>[] = [];
  const joinedGroupPromises: Promise<any>[] = [];
  for (const cKey of createdGroupKeys) {
    createdGroupPromises.push(db.ref('groups/' + cKey).once('value'));
  }
  for (const jKey of joinedGroupKeys) {
    joinedGroupPromises.push(db.ref('groups/' + jKey).once('value'));
  }

  const createdGroups = Promise.all(createdGroupPromises).then((groups) => {
    return processResolvedGroups(createdGroupKeys, groups);
  });
  const joinedGroups = Promise.all(joinedGroupPromises).then((groups) => {
    return processResolvedGroups(joinedGroupKeys, groups);
  });

  return Promise.all([createdGroups, joinedGroups]).then((result) => {
    return {
      created_groups: result[0],
      joined_groups: result[1],
    };
  });
}

export function getGroupsImpl(data: any, context: CallableContext) {
  if (context.auth.uid && context.auth.uid.length > 0) {
    const db = admin.database();
    const userRef = db.ref('users/');
    return userRef.child(context.auth.uid).once('value').catch((err) => {
      return new HttpsError(err.message);
    }).then((snapshot: any): Promise<any> => {
      if (snapshot.val()) {
        return getGroupsForUser(snapshot.val(), context.auth.uid);
      }

      return createNewUser(userRef, context);
    });
  } else {
    return {
      error: 'Error authenticating the user',
    };
  }
}