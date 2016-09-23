#!/bin/bash

status=$(minishift status)

if [ $status = "Running" ]; then
   echo "## Minishift is already running ... No further action is required"
else
  echo "## Start Minishift !"
  minishift start
fi