import * as admin from 'firebase-admin'
import { generateUuid } from '../utilities/uuid'
import { Group } from '../models/group'
import { HttpsError, CallableContext } from 'firebase-functions/lib/providers/https';
import { DataSnapshot } from 'firebase-functions/lib/providers/database';
import { getYelpResultsForGroup } from '../utilities/yelp-requester';
import { YelpResult } from '../models/yelp-result';
import { IGroup, IVote, IMatch } from '../models/interfaces';


export function createGroupImpl(data: any, context: CallableContext) {
  if (context.auth.uid && context.auth.uid.length > 0) {
    const newGroup: Group = new Group(data.name, data.description, data.activity,
      data.startingLocation, data.latitude, data.longitude, data.results,
      data.conclusion, data.expiration);

    if (!newGroup.isValid()) {
      return {
        error: 'Invalid group!',
      };
    }

    const newGroupUuid = generateUuid();
    let yelpMatches: YelpResult[] = [];

    const db = admin.database();
    const groupRef = db.ref('groups/' + newGroupUuid);
    const userRef = db.ref('users/' + context.auth.uid);
    const matchesRef = db.ref('matches/');
    const votesRef = db.ref('match_votes/');
    return getYelpResultsForGroup(newGroup).then((matches) => {
      yelpMatches = matches;
      return groupRef.once('value');
    }).catch((err) => {
      return new HttpsError(err.message);
    }).then((groupSnapshot: DataSnapshot) => {
      if (groupSnapshot.val()) {
        return {
          error: 'Oh no! There was an error creating your group!',
        };
      }

      return userRef.once('value').catch((userErr) => {
        return new HttpsError(userErr.message);
      }).then((userSnapshot: DataSnapshot): any|Promise<any> => {
        const userInstance = userSnapshot.val();
        if (!userInstance) {
          return {
            error: 'Error getting your user info!',
          };
        }

        //Create Group Entry
        const groupEntry: IGroup = {
          "name": newGroup.name,
          "query": newGroup.activity,
          "description": newGroup.description,
          "location": {
            "latitude": newGroup.location.latitude,
            "longitude": newGroup.location.longitude
          },
          "numberResults": newGroup.maxResults,
          "voteConclusion": newGroup.voteConclusion,
          "daysToExpire": newGroup.expiration,
          "members": {},
          "matches": {},
        };
        const newMatchVotes: IVote = {};
        const newMatches: IMatch = {};
        groupEntry.members[context.auth.uid] = true;
        for(const business of yelpMatches) {
          //add business match ids to the group listing
          groupEntry.matches[business.id] = true;

          //add the creator to the list of votes that need to happen
          newMatchVotes[newGroupUuid + '|' + business.id] = {
            [context.auth.uid]: false,
          };

          //store details of the match in the database
          newMatches[newGroupUuid + '|' + business.id] = {
            fetchTime: (new Date()).getTime(),
            details: business,
          };
        }

        // Determine whether an insert or update needs to happen based on the presence
        // of a group or not. If there is no group the "tables" must be created and an
        // insert needs to happen. If there is a result, we need to update so that
        // existing data does not get blown away
        return db.ref('groups/').limitToFirst(1).once('value').catch((saveErr) => {
          return new HttpsError(saveErr);
        }).then((snapshot: DataSnapshot) => {
          if (snapshot.val()) {
            //There is already data, perform an update
            db.ref('groups/').child(newGroupUuid).set(groupEntry);
            for (const key in newMatches) {
              if (newMatches.hasOwnProperty(key)) {
                matchesRef.child(key).set(newMatches[key]);
                votesRef.child(key).set(newMatchVotes[key]);
              }
            }
          } else {
            //there is currently no data, perform a set
            const newGroupEntry: {[key:string]: IGroup} = {};
            newGroupEntry[newGroupUuid] = groupEntry;
            db.ref('groups/').set(newGroupEntry);
            matchesRef.set(newMatches);
            votesRef.set(newMatchVotes);
          }

          //Add the group as a created group for the user
          if (!userInstance.created_groups) {
            userInstance.created_groups = {};
          }
          userInstance.created_groups[newGroupUuid] = true;
          userRef.set(userInstance);

          return {
            groupId: newGroupUuid,
          };
        });
      });
    });
  } else {
    return {
      error: 'Error authenticating the user',
    };
  }
}
