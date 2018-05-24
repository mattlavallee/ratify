import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
import { getGroupsImpl } from './actions/get-groups';

const serviceAccount = require('../ratify-service-key.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://ratify-266f4.firebaseio.com/',
});

export const getGroups = functions.https.onCall(getGroupsImpl);
