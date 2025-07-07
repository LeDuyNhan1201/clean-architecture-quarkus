#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
if [ "$1" ]; then
  env_file=${DIR}/helper/idp_config-$1.sh
else
  env_file=${DIR}/helper/idp_config.sh
fi
echo "Processing $env_file"
source "$env_file"
source "${DIR}"/helper/functions.sh

#-------------------------------------------------------------------------------
# Update cli permission to be executable
update_cli_permissions

# Create client files to be used for produce/consume
create_client_files

create_env_file

docker compose -f ../sasl_ssl_oauth.yml up -d
#docker compose -f ../identity-server.yml up -d