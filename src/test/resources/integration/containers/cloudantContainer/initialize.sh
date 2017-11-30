#!/bin/sh
yum makecache fast
yum -y install -y nodejs
npm install npm -g
yum -y update openssl
npm rebuild
npm install -g @cloudant/couchbackup
curl -X PUT http://admin:pass@localhost:80/test
export COUCH_URL=http://admin:pass@localhost:80
cat /app/testData.txt | couchrestore
