import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp(functions.config().firebase);

export const getGroups = functions.https.onRequest((request, response) => {
  admin.auth().verifyIdToken(request.params['uuid'])
    .then((decodedToken) => {
      response.status(200);
      response.json({
        data: 'Successful Authentication!!!',
      });
    })
    .catch(() => {
      response.status(403);
      response.json({
        data: '',
        error: 'Error authenticating the user',
      });
    })
});
