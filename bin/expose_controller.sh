#!/usr/bin/env bash

oc adm policy add-cluster-role-to-user cluster-admin system:serviceaccount:default:exposecontroller
oc adm policy add-cluster-role-to-group cluster-reader system:serviceaccounts

cat <<EOF | oc create -f -
apiVersion: "v1"
data:
  config.yml: |-
    exposer: Route
    domain: xip.io
kind: "ConfigMap"
metadata:
  name: "exposecontroller"
EOF

oc create -f http://central.maven.org/maven2/io/fabric8/devops/apps/exposecontroller/2.2.255/exposecontroller-2.2.255-kubernetes.yml