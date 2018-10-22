import * as  admin from 'firebase-admin';

describe('DB Instance', () => {
  it('gets a db instance and caches the response', () => {
    const spy = jest.spyOn(admin, 'database', 'get').mockReturnValueOnce(() => {return {};});
    const {getDatabase} = require('./db-instance');

    getDatabase();
    expect(spy).toHaveBeenCalledTimes(1);

    getDatabase();
    expect(spy).toHaveBeenCalledTimes(1);
  });
});