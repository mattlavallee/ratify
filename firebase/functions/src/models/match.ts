export interface IMatch {
  [key: string]: {
    fetchTime: Number;
    details: IMatchDetails;
  }
}

export interface ISingleMatch {
  fetchTime: number;
  details: IMatchDetails;
};

export interface IMatchDetails {
  id: string;
  name: string;
  businessImage: string;
  address: string;
  rating: number;
  price: string;
};
