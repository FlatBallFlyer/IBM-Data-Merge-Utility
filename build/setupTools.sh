#!/bin/bash

## This script installs gulp and bower using NPM, both of which are needed
## Depending on how node&npm are installed you might need to execute this script with superuser privileges
set -evx
echo "Install bower"
npm install -g bower
echo "Test bower"
bower --version
echo "Install gulp"
npm install -g gulp
echo "Test gulp"
gulp --version
echo "OK"