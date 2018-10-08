export interface IVote {
  [key: string]: {
    [userId: string]: boolean
  }
};
