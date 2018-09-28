import { generateUuid } from '../utilities/uuid'
import { getGroup, insertGroup } from '../database/group';
import { getUser, updateUser } from '../database/user';
import { Group } from '../models/group'
import { User } from '../models/user';
import { HttpsError, CallableContext } from 'firebase-functions/lib/providers/https';
import { getYelpResultsForGroup } from '../utilities/yelp-requester';
import { YelpResult } from '../models/yelp-result';
import { IGroup, IVote, IMatch, IResult } from '../models/interfaces';
import { insertUserVotes } from '../database/votes';
import { insertMatches } from '../database/matches';


export function createGroupImpl(data: any, context: CallableContext) {
  if (context.auth.uid && context.auth.uid.length > 0) {
    const newGroup: Group = new Group(data.name, data.type, data.description, data.activity,
      data.startingLocation, data.latitude, data.longitude, data.results,
      data.conclusion, data.expiration);

    if (!newGroup.isValid()) {
      return Promise.resolve({
        error: 'Invalid group!',
      });
    }

    const newGroupUuid = generateUuid();

    return getYelpResultsForGroup(newGroup).then((matches: YelpResult[]): Promise<IResult> => {
      const groupPromise: Promise<IGroup> = getGroup(newGroupUuid);
      const userPromise: Promise<User> = getUser(context.auth.uid);

      return Promise.all([groupPromise, userPromise]).catch((err: HttpsError) => err)
        .then((results: Array<IGroup|User>|HttpsError): Promise<IResult> => {
          const group: IGroup = results[0];
          const user: User = results[1];
          
          if (group) {
            return Promise.resolve({error: 'Oh no! There was an error creating your group!'});
          } else if (!user) {
            return Promise.resolve({error: 'Error getting your user info!'});
          }

          //Create Group Entry
          const groupEntry: IGroup = {
            "name": newGroup.name,
            "type": newGroup.type,
            "query": newGroup.activity,
            "description": newGroup.description,
            "location": {
              "latitude": newGroup.location.latitude,
              "longitude": newGroup.location.longitude
            },
            "numberResults": newGroup.maxResults,
            "voteConclusion": newGroup.voteConclusion.getTime(),
            "daysToExpire": newGroup.expiration,
            "members": {},
            "matches": {},
          };
          const newMatchVotes: IVote = {};
          const newMatches: IMatch = {};
          groupEntry.members[context.auth.uid] = true;
          for(const business of matches) {
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

          //either insert or update db values, setting all necessary values for the new group
          const newGroupPromise = insertGroup(newGroupUuid, groupEntry);
          const matchPromise = insertMatches(newMatches);
          const votePromise = insertUserVotes(newMatchVotes);

          if (!user.created_groups) {
            user.created_groups = {};
          }
          user.created_groups[newGroupUuid] = true;
          const updateUserPromise = updateUser(context.auth.uid, user);
          return Promise.all([newGroupPromise, matchPromise, votePromise, updateUserPromise]).then(() => {
            return {groupId: newGroupUuid};
          });
        });
    });
  } else {
    return Promise.resolve({
      error: 'Error authenticating the user',
    });
  }
}
