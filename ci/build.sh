#!/usr/bin/env bash
#shellcheck disable=SC2039

set -exuo pipefail

[[ -n "${CI_TAG:=}" ]] && { echo "Skip build"; exit 0; }

image_prefix="eu.gcr.io/akvo-lumen/hortinvest"

cd frontend
docker build \
       --tag "${image_prefix}/frontend:latest" \
       --tag "${image_prefix}/frontend:${CI_COMMIT}" .

cd ../lumen-datasets-periodic-updates
./ci/build.sh
