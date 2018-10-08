interface IYelpFullResult {
  id: string,
  alias: string,
  name: string,
  image_url: string,
  is_closed: boolean,
  url: string,
  review_count: number,
  categories: any[],
  rating: number,
  coordinates: {[key:string]: number},
  transactions: any[],
  price: string,
  location: {
    address1: string,
    address2: string,
    address3: string,
    city: string,
    zip_code: string,
    country: string,
    state: string,
    display_address: string[]
  },
  phone: string,
  display_phone: string,
  distance: number
};

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