import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp(functions.config().firebase);

export const getGroups = functions.https.onCall((data, context) => {
  if (context.auth.uid && context.auth.uid.length > 0) {
    return {
      data: 'Successful Authentication!!!',
    };
  } else {
    return {
      data: '',
      error: 'Error authenticating the user',
    };
  }
});
