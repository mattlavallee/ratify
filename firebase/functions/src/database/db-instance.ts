import * as admin from 'firebase-admin';
let db: admin.database.Database;
export function getDatabase() {
  if (!db) {
    db = admin.database();
  }
  return db;
}
