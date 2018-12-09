export interface IVote {
  [key: string]: {
    [userId: string]: boolean;
  }
};

export interface IDetailedGroupVotes {
  [userId: string]: {
    [matchId: string]: boolean;
  }
};
