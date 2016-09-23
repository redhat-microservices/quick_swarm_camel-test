#!/usr/bin/env bash

echo "## Start minishift if not yet done"
./bin/start_minishift.sh

echo "## Create the docker image, openshift templates & deploy"
mvn -Popenshift-local-deploy
