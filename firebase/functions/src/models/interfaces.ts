export interface IYelpFullResult {
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

export interface IVote {
  [key: string]: {
    [userId: string]: boolean
  }
};

export interface IMatch {
  [key: string]: {
    fetchTime: Number,
    details: {
      id: string,
      name: string,
      businessImage: string,
      address: string,
      rating: number,
      price: string,
    }
  }
}

export interface IGroup {
  name: string,
  query: string,
  description: string,
  location: {
    latitude: Number,
    longitude: Number,
  },
  numberResults: Number,
  voteConclusion: Date,
  daysToExpire: Number,
  members: {
    [userId: string]: boolean
  },
  matches: {
    [matchId: string]: boolean
  }
};

export interface IResult {
  error?: string,
  [key: string]: string,
};