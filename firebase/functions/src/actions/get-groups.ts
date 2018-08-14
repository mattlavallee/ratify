import { getUser, createUser } from '../database/user';
import { getGroupsForUser } from '../database/group';
import { User } from '../models/user';
import { IUserGroup } from '../models/group';
import { IResult } from '../models/interfaces';
import { HttpsError, CallableContext } from 'firebase-functions/lib/providers/https';

export function getGroupsImpl(data: any, context: CallableContext): Promise<IUserGroup|IResult> {
  if (context.auth.uid && context.auth.uid.length > 0) {
    return getUser(context.auth.uid).catch((userErr: HttpsError) => userErr)
      .then((userDef: User): Promise<IUserGroup> => {
        if (userDef) {
          return getGroupsForUser(userDef);
        }

        return createUser(context.auth.uid, new User(context.auth.uid));
      });
  } else {
    return Promise.resolve({
      error: 'Error authenticating the user',
    });
  }
}