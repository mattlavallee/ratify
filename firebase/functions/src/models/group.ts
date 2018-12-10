import { IDetailedGroupVotes } from "./vote";
import { IMatchDetails, ISingleMatch } from "./match";

export interface IGroup {
  name: string,
  type: string,
  query: string,
  description: string,
  location: {
    latitude: number,
    longitude: number,
  },
  numberResults: number,
  voteConclusion: number,
  daysToExpire: number,
  members: {
    [userId: string]: boolean
  },
  matches: {
    [matchId: string]: boolean
  }
};

export interface IUserGroup {
  created_groups: {[groupId: string]: IGroup},
  joined_groups: {[groupId: string]: IGroup},
};

export class GroupRequest {
  public name: string;
  public type: string;
  public description: string;
  public activity: string;
  public location: {[key: string]: any} = {
    name: '',
    latitude: 0,
    longitude: 0
  };
  public maxResults: number;
  public voteConclusion: Date;
  public expiration: number;

  constructor(name: string, type: string, descr: string, activity: string, location: string, 
             latitude: number, longitude: number, results: number, conclusion: number, 
             expiration: number) {
    this.name = name;
    this.type = type;
    this.description = descr;
    this.activity = activity;
    this.location.name = location;
    this.location.latitude = latitude;
    this.location.longitude = longitude;
    this.maxResults = results;
    this.voteConclusion = new Date(conclusion);
    this.expiration = expiration;
  }

  isValid() {
    if (this.name && this.name.length &&
        this.type && this.type.length &&
        this.description && this.description.length &&
        this.activity && this.activity.length
        && this.location.name.length &&
        this.maxResults > 0 && this.maxResults <= 30 &&
        !isNaN(this.voteConclusion.getTime()) &&
        this.expiration > 0 && this.expiration <= 14) {
      return true;
    }

    return false;
  }
}

export class DetailedGroup extends GroupRequest {
  public userVotes: IDetailedGroupVotes = {};
  public matches: {[matchId: string]: IMatchDetails} = {};

  constructor(group: IGroup) {
    super(group.name, group.type, group.description, group.query, '_', group.location.latitude,
      group.location.longitude, group.numberResults, group.voteConclusion, group.daysToExpire);
  }

  public setVoteState(votes: IDetailedGroupVotes) {
    this.userVotes = votes;
  }

  public setMatches(inflatedMatches: ISingleMatch[]) {
    inflatedMatches.forEach((match: ISingleMatch) => {
      this.matches[match.details.id] = match.details;
    });
  }
}