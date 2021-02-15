#!/usr/bin/env bash

set -eu

function log {
   echo "$(date +"%T") - INFO - $*"
}

export PROJECT_NAME=akvo-lumen

if [[ "${CI_BRANCH}" != "automatic-updates" ]]; then
    exit 0
fi

#if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
#    exit 0
#fi

log Authentication with gcloud and kubectl
gcloud auth activate-service-account --key-file=/home/semaphore/.secrets/gcp.json --project "${PROJECT_NAME}"
gcloud config set container/cluster europe-west1-d
gcloud config set compute/zone europe-west1-d
gcloud config set container/use_client_certificate False

## TODO!!! Change to prod! Decide if we publish to test or not
#if [[ "${TRAVIS_BRANCH}" == "master" ]]; then
#    log Environment is production
#    log Project not deployed to production cluster. Exiting now.
#    exit 0
#else
if [[ "${CI_BRANCH}" == "automatic-updates"]]; then
    log Environement is test
    gcloud container clusters get-credentials test
fi
#fi

log Pushing images
gcloud auth configure-docker
docker push "eu.gcr.io/${PROJECT_NAME}/hortinvest-lumen-updates:${CI_COMMIT}"
cd lumen-datasets-periodic-updates
sed -e "s/\${CI_COMMIT}/$CI_COMMIT/g" ci/k8s/cronjob.yaml.template > cronjob.yaml.donotcommit

kubectl apply -f cronjob.yaml.donotcommit
