#!/bin/bash

set -eux

IMAGE_NAME="akvo/cljs-dev:lo"

cd frontend/
docker build -t $IMAGE_NAME .
docker run --rm -ti -v "$(pwd)":/app $IMAGE_NAME shadow-cljs release main
