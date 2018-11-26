import * as yelpRequester from '../utilities/yelp-requester';
import { CallableContext } from 'firebase-functions/lib/providers/https';
import { YelpResult, IYelpFullResult } from '../models/yelp-result';

describe('Preview Group', () => {
  it('throws an error if unauthorized', (done) => {
    const {previewGroupImpl} = require('./preview-group');
    previewGroupImpl({}, {
      auth: {uid: null},
    } as CallableContext).then((result) => {
      expect(result.error).toEqual('Error authenticating the user');
      done();
    });
  });

  it('returns preview results successfully', (done) => {
    jest.spyOn(yelpRequester, 'getYelpResultsForGroup').mockReturnValueOnce(Promise.resolve([
      new YelpResult({
        id: 'match1',
        name: 'A Match',
        image_url: 'match.png',
        rating: 4.3,
        price: '$$',
        location: {
          display_address: ['ABC 123 St']
        }} as IYelpFullResult),
    ]));
    const {previewGroupImpl} = require('./preview-group');

    previewGroupImpl({
      activity: 'bar',
      latitude: 68,
      longitude: 73,
    }, {
      auth: {uid: 'test'}
    } as CallableContext).then((result) => {
      expect(result).toEqual([new YelpResult({
        id: 'match1',
        name: 'A Match',
        image_url: 'match.png',
        rating: 4.3,
        price: '$$',
        location: {
          display_address: ['ABC 123 St']
        }
      } as IYelpFullResult)]);
      done();
    });
  });
});