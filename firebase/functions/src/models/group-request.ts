import {IGroup} from './interfaces';

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