#!/bin/bash
set -ev

git clone https://github.com/inf295uci-2015/primitive-hamcrest.git  
cd primitive-hamcrest  
mvn install
cd ..
git clone https://github.com/spideruci/tacoco.git
cd tacoco
mvn install
cd ..
