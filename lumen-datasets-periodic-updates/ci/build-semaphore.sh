#!/usr/bin/env bash
set -eu

function log {
   echo "$(date +"%T") - INFO - $*"
}

export PROJECT_NAME=akvo-lumen

if [ -z "$CI_COMMIT" ]; then
    export TRAVIS_COMMIT=local
fi

log Creating Production image
cd lumen-datasets-periodic-updates
docker build --rm=false -t "eu.gcr.io/${PROJECT_NAME}/hortinvest-lumen-updates:${CI_COMMIT}" .

log Done
