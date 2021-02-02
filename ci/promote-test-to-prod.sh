#!/usr/bin/env bash

set -euo pipefail

deployment_name="hortinvest"
deployment_version_label="hortinvest-version"
github_project="hortinvest"
notification="zulip"
zulip_stream="K2 Engine"

docker run \
       --rm \
       --volume "${HOME}/.config:/home/akvo/.config" \
       --volume "$(pwd):/app" \
       --env ZULIP_CLI_TOKEN \
       --interactive \
       --tty \
       akvo/akvo-devops:20201203.085214.79bec73 \
       promote-test-to-prod.sh "${deployment_name}" "${deployment_version_label}" "${github_project}" "${notification}" "${zulip_stream}"
