import axios from 'axios';
import {GroupRequest} from '../models/group';
import { generateUuid } from './uuid';
import {getYelpResultsForGroup} from './yelp-requester';
import {YelpResult} from '../models/yelp-result';

describe('Utility Functions', () => {
    describe('UUID', () => {
        it('generates a uuid', () => {
            const uuid = generateUuid();
            expect(uuid).toBeDefined();
            expect(typeof uuid).toEqual('string');
        });
    });

    describe('Yelp Requester', () => {
        it('makes a request to the yelp api', () => {
            const stub = jest.spyOn(axios, 'get');
            const date = new Date();
            const testGroup = new GroupRequest('test', 'restaurant', 'test', 
                'lunch', 'here', 1, 1, 10, date.getTime(), date.getTime());
            getYelpResultsForGroup(testGroup);

            expect(stub.mock.calls[0][0]).toEqual(
                'https://api.yelp.com/v3/businesses/search?radius=4000&term=lunch&latitude=1&longitude=1&limit=10&sort_by=distance'
            );
        });

        it('returns results from yelp', (done) => {
            const stub = jest.spyOn(axios, 'get');
            stub.mockReturnValueOnce(Promise.resolve({
                data: {
                    businesses: [{
                        id: 'foo',
                        name: 'Foo Name',
                        image_url: 'img',
                        rating: 4.3,
                        price: '$',
                        location: {
                            display_address: ['123 Sesame St'],
                        },
                    }, {
                        id: 'bar',
                        name: 'Bar Name',
                        image_url: 'test img',
                        rating: 2.2,
                        price: '$$',
                        location: {
                            display_address: ['ABC 123'],
                        },
                    }],
                },
            }));

            const date = new Date();
            const testGroup = new GroupRequest('test', 'restaurant', 'test', 
                'lunch', 'here', 1, 1, 10, date.getTime(), date.getTime());
            getYelpResultsForGroup(testGroup).then(results => {
                expect(results.length).toEqual(2);
                expect((results[0] as YelpResult).name).toEqual('Foo Name');
                expect((results[1] as YelpResult).name).toEqual('Bar Name');
                done();
            });
        });
    });
});