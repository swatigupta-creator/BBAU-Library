const fs=require('node:fs');
const crypto=require('node:crypto');
const file='.env';
if(fs.existsSync(file)){
  console.log('.env already exists; keeping its current credentials.');
  process.exit(0);
}
const adminPassword=crypto.randomBytes(15).toString('base64url');
const sessionSecret=crypto.randomBytes(48).toString('hex');
fs.writeFileSync(file,[
  'PORT=3000',
  'NODE_ENV=development',
  'SESSION_SECRET='+sessionSecret,
  'ADMIN_EMAIL=admin@bbau.ac.in',
  'ADMIN_PASSWORD='+adminPassword,
  'DATABASE_FILE=./data/bbau-library.sqlite',
  ''
].join('\n'),{mode:0o600});
console.log('Created private .env configuration.');
console.log('Administrator email: admin@bbau.ac.in');
console.log('One-time administrator password: '+adminPassword);
console.log('Save this password now; it is stored only in the ignored .env file.');
