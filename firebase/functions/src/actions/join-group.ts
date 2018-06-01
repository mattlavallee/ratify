import * as admin from 'firebase-admin'
import { HttpsError, CallableContext } from 'firebase-functions/lib/providers/https';
import { DataSnapshot } from 'firebase-functions/lib/providers/database';

export function joinGroupImpl(data: any, context: CallableContext) {
  if (context.auth.uid && context.auth.uid.length > 0) {
    const db = admin.database();
    const userRef = db.ref('users/');
    const groupRef = db.ref('groups/' + data.groupCode);

    return groupRef.once('value').catch((err) => {
      return new HttpsError(err.message);
    }).then((snapshot: DataSnapshot) => {
      if (snapshot.val()) {
        return userRef.child(context.auth.uid).once('value').then((uSnap: DataSnapshot): any => {
          const user = uSnap.val();
          if (uSnap.val()) {
            if ((user.createdGroups && user.createdGroups[data.groupCode]) ||
              (user.joinedGroups && user.joinedGroups[data.groupCode])) {
                return {
                  error: 'Hey! You\'ve already joined this group!',
                };
              }
            
            user.joinedGroups[data.groupCode] = true;
            return userRef.update(user).then(() => {
              return {
                data: 'done!',
              };
            });
          }

          return {
            error: 'Error retrieving user information',
          };
        });
      }

      return {
        error: 'Aww, shucks, ' + data.groupCode + ' doesn\'t exist',
      };
    });
  } else {
    return {
      error: 'Error authenticating the user',
    };
  }
}
