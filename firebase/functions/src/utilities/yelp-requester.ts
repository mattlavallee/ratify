const yelpApi = require('../../ratify-yelp-key.json');
import axios from 'axios';
import { Group } from '../models/group';
import { YelpResult } from '../models/yelp-result';

const yelpBusinessSearch: string = 'https://api.yelp.com/v3/businesses/search?radius=4000';

export function getYelpResultsForGroup(group: Group): Promise<YelpResult[]> {
  const businessSearchUrl = yelpBusinessSearch + 
    '&term=' + group.activity +
    '&latitude=' + group.location.latitude +
    '&longitude=' + group.location.longitude +
    '&limit=' + group.maxResults +
    '&sort_by=distance';

  return axios.get(businessSearchUrl, {
    headers: {
      'Authorization': 'Bearer ' + yelpApi.apiKey,
    },
  }).then((result: any): YelpResult[] => {
    const matches: YelpResult[] = [];
    for (const business of result.data.businesses) {
      matches.push(new YelpResult(business));
    }
    return matches;
  }).catch((err: Error) => {
    console.log(err.message);
    return [];
  });
}