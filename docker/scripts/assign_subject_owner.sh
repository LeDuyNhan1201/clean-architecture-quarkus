#!/bin/bash

CLIENT_APP_ID=client_app1
CLIENT_APP_SECRET=client_app1_secret
SUBJECT_NAME="$1"

# Lấy access token từ IDP
auth_token=$(curl -s -d "client_id=$SUPERUSER_CLIENT_ID" \
                  -d "client_secret=$SUPERUSER_CLIENT_SECRET" \
                  -d "grant_type=client_credentials" \
                  --insecure "$IDP_TOKEN_ENDPOINT" \
                  --cert /certs/client.crt --key /certs/client.key --cacert /certs/ca.crt | \
             grep -Po '"access_token": *\K"[^"]*"' | grep -o '[^"]*')

echo "Access Token: $auth_token"

MDS_RBAC_ENDPOINT=https://broker1:8091/security/1.0/principals

# Gán quyền ResourceOwner cho topic
curl -X --insecure POST "$MDS_RBAC_ENDPOINT/User:$CLIENT_APP_ID/roles/ResourceOwner/bindings" \
  -H "Authorization: Bearer $auth_token" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{
    \"scope\": {
      \"clusters\": {
        \"kafka-cluster\": \"vHCgQyIrRHG8Jv27qI2h3Q\",
        \"schema-registry-cluster\": \"schema-registry-cluster\"
      }
    },
    \"resourcePatterns\": [
      {
        \"resourceType\": \"Subject\",
        \"name\": \"$SUBJECT_NAME\",
        \"patternType\": \"PREFIXED\"
      }
    ]
  }"
