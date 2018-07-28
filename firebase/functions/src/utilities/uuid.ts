const shortid = require('shortid');

export function generateUuid(): string {
  return shortid.generate();
}