import { CallableContext } from 'firebase-functions/lib/providers/https';
import { GroupRequest } from '../models/group';
import { getYelpResultsForGroup } from '../utilities/yelp-requester';
import { YelpResult } from '../models/yelp-result';

export function previewGroupImpl(data: any, context: CallableContext): Promise<any> {
  if (context.auth.uid && context.auth.uid.length > 0) {
    const newGroup: GroupRequest = new GroupRequest('', '', '', data.activity,
      '', data.latitude, data.longitude, data.maxResults, -1, -1);

    return getYelpResultsForGroup(newGroup).then((matches: YelpResult[]) => matches);
  } else {
    return Promise.resolve({
      error: 'Error authenticating the user',
    });
  }
}