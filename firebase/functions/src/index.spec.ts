import * as admin from 'firebase-admin';

describe('Entrypoint', () => {
  it('initializes the endpoints properly', () => {
    jest.spyOn(admin, 'initializeApp');
    const endpoints = require('./index');

    const fns = Object.keys(endpoints);
    for(const fn of fns) {
      expect(typeof endpoints[fn]).toEqual('function');
    }
  });
});
