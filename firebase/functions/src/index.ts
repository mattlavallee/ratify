import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
import { getGroupsImpl } from './actions/get-groups';
import { joinGroupImpl } from './actions/join-group';
import { createGroupImpl } from './actions/create-group';
import { previewGroupImpl } from './actions/preview-group';
import { getGroupByIdImpl } from './actions/get-group-by-id';
import { setGroupVotesImpl } from './actions/set-group-votes';

const serviceAccount = require('../ratify-service-key.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://ratify-266f4.firebaseio.com/',
});

export const getGroups = functions.https.onCall(getGroupsImpl);
export const joinGroup = functions.https.onCall(joinGroupImpl);
export const createGroup = functions.https.onCall(createGroupImpl);
export const previewGroupResults = functions.https.onCall(previewGroupImpl);
export const getGroupById = functions.https.onCall(getGroupByIdImpl);
export const setGroupVotes = functions.https.onCall(setGroupVotesImpl);
