#!/usr/bin/env bash

echo "## Delete Minishift"
minishift delete

echo "## Backup ~/.kube directory before to delete it"
cp -rf ~/.kube/ ~/.kube.bk/
rm -rf ~/.kube/

echo "## Create & start minishift"
minishift start --deploy-registry=true --deploy-router=true --memory=4048 --vm-driver="xhyve"