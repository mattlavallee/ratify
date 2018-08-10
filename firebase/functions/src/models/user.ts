export interface IUserGroupMap {[groupId: string]: boolean}

export class User {
  public name: string;
  public created_groups?: IUserGroupMap;
  public joined_groups?: IUserGroupMap;

  constructor(name: string, createdGroups?: IUserGroupMap, joinedGroups?: IUserGroupMap) {
    this.name = name;
    this.created_groups = createdGroups || {};
    this.joined_groups = joinedGroups || {};
  }
}