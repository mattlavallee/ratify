import { IYelpFullResult } from "./interfaces";

export class YelpResult {
  public id: string;
  public name: string;
  public businessImage: string;
  public address: string;
  public rating: number;
  public price: string;

  constructor(result: IYelpFullResult) {
    this.id = result.id;
    this.name = result.name;
    this.businessImage = result.image_url;
    this.address = result.location.display_address.join('\n');
    this.rating = result.rating;
    this.price = result.price || '?';
  }
}