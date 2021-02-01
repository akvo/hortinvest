#!/usr/bin/env bash

set -eu

IMAGE_NAME="akvo/hortinvest:latest"
CONTAINER_NAME="akvo-hortinvest"

docker run --name $CONTAINER_NAME -d -p 8080:80 $IMAGE_NAME
