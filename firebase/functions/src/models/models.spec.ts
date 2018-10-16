import {GroupRequest} from './group';
import {User, IUserGroupMap} from './user';
import {YelpResult, IYelpFullResult} from './yelp-result';

describe('Models', () => {
  it('Group', () => {
    const date = new Date();
    const test = new GroupRequest('Test', 'restaurant', 'test obj', 'lunch', 
      '123 Sesame St', 12, 13, 5, date.getTime(), 3);
    expect(test.name).toEqual('Test');
    expect(test.voteConclusion).toEqual(date);
    expect(test.location).toEqual({
      latitude: 12,
      longitude: 13,
      name: '123 Sesame St'
    });

    expect(test.isValid()).toEqual(true);

    test.maxResults = 45;
    expect(test.isValid()).toEqual(false);
  });

  it('User', () => {
    const test = new User('Fifi');
    expect(test.name).toEqual('Fifi');
    expect(test.created_groups).toEqual({});
    expect(test.joined_groups).toEqual({});

    const createdGroups: IUserGroupMap = {a: true};
    const joinedGroups: IUserGroupMap = {b: true};
    const test2 = new User('Fifi', createdGroups, joinedGroups);
    expect(test2.name).toEqual('Fifi');
    expect(test2.created_groups).toEqual(createdGroups);
    expect(test2.joined_groups).toEqual(joinedGroups);
  });

  it('Yelp Result', () => {
    const fullResult: IYelpFullResult = {
      id: '123',
      alias: '123abc',
      name: 'Oscar',
      image_url: 'garbage_can.png',
      is_closed: false,
      url: 'sesamest.com',
      review_count: 10,
      rating: 5,
      coordinates: {
        latitude: 1,
        longitude: 2,
      },
      transactions: [],
      categories: [],
      price: '$',
      location: {
        address1: '',
        address2: '',
        address3: '',
        city: 'test',
        zip_code: '123456',
        country: 'US',
        state: 'OH',
        display_address: ['123 Sesame St', 'Osh Kosh, Ohio'],
      },
      phone: '123-456-7890',
      display_phone: '',
      distance: 4,
    };
    const test = new YelpResult(fullResult);
    expect(test).toEqual({
      id: fullResult.id,
      name: fullResult.name,
      businessImage: fullResult.image_url,
      address: '123 Sesame St\nOsh Kosh, Ohio',
      rating: fullResult.rating,
      price: fullResult.price,
    })
  });
});