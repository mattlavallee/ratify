import * as functions from 'firebase-functions';

export const test = functions.https.onRequest((request, response) => {
  response.status(200);
  response.json({
    message: 'success!',
  });
});
