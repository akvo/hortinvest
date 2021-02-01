#!/usr/bin/env bash

set -eu

IMAGE_NAME="akvo/hortinvest:latest"

cd frontend/
docker build -t $IMAGE_NAME .
