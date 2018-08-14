import {getDatabase} from './db-instance';
import {errorCodes} from '../utilities/firebase-error-codes';
import { DataSnapshot } from '../../node_modules/firebase-functions/lib/providers/database';
import { HttpsError } from '../../node_modules/firebase-functions/lib/providers/https';
import { User } from '../models/user';
import { IUserGroup } from '../models/group';
import { database } from '../../node_modules/firebase-admin';

let userReference: database.Reference;
function getUserDBReference() {
  if (!userReference) {
    userReference = getDatabase().ref('users/');
  } 
  return userReference;
}

export function getUser(userId: string): Promise<User> {
  return getUserDBReference().child(userId).once('value').catch((err: Error) => {
    return new HttpsError((<any>errorCodes).internal, err.message);
  }).then((snapshot: DataSnapshot): User => {
    return snapshot.val() as User;
  });
}

export function createUser(userId: string, model: User): Promise<IUserGroup> {
  return getUserDBReference().limitToFirst(1).once('value').catch((saveErr: Error) => {
    return new HttpsError((<any>errorCodes).internal, saveErr.message);
  }).then((userExistsSnapshot: DataSnapshot): Promise<IUserGroup> => {
    let userSavePromise: Promise<any>;
    if (userExistsSnapshot.val()) {
      //update
      userSavePromise = getUserDBReference().child(userId).set(model);
    } else {
      //create
      const newUserObj: any = {};
      newUserObj[userId] = model;
      userSavePromise = getUserDBReference().set(newUserObj);
    }

    return userSavePromise.then((): IUserGroup => {
      return {
        created_groups: {},
        joined_groups: {},
      };
    });
  });
}

export function updateUser(userId: string, model: User): Promise<any> {
  return getUserDBReference().child(userId).set(model);
}