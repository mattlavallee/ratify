import * as dbInstance from './db-instance';
// let returnVal = false;
// const dbMock = {
//   ref: () => {
//     return {
//       limitToFirst: () => {
//         return {
//           once: () => Promise.resolve({
//             val: () => {
//               return returnVal ? {} : null;
//             },
//           }),
//         };
//       },
//       set: () => Promise.resolve(),
//       child: () => {
//         return {
//           set: () => Promise.resolve(),
//         };
//       }
//     };
//   },
// };

describe('Votes Database Handler', () => {
  // it('Sets the matches on new match db section', (done) => {
  //   returnVal = false;
  //   jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
  //   const {insertMatches} = require('./matches');

  //   insertMatches([{}]).then((response) => {
  //     expect(response).toEqual(true);
  //     done();
  //   });
  // });

  // it('sets matches on an existing match db section', (done) => {
  //   returnVal = true;
  //   jest.spyOn(dbInstance, 'getDatabase').mockReturnValueOnce(dbMock);
  //   const {insertMatches} = require('./matches');

  //   insertMatches([{a: true}]).then((response) => {
  //     expect(response).toEqual(true);
  //     done();
  //   });
  // });
});