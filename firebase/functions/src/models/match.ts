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
