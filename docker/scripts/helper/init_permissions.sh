#!/bin/bash

_assign_sr_role_bindings() {
  curl -X --insecure POST $1/User:$SR_CLIENT_ID/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'
  curl -X --insecure POST $1/User:$SR_CLIENT_ID/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q", "schema-registry-cluster":"schema-registry-cluster"}}'
  curl -X --insecure POST $1/User:$SR_CLIENT_ID/roles/ResourceOwner/bindings -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"scope":{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}, "resourcePatterns":[{"resourceType":"Topic", "name":"_confluent-command", "patternType":"LITERAL"}]}'
  curl -X --insecure POST $1/User:$SR_CLIENT_ID/roles/ResourceOwner/bindings -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"scope":{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}, "resourcePatterns":[{"resourceType":"Topic", "name":"_exporter_configs", "patternType":"LITERAL"}]}'
  curl -X --insecure POST $1/User:$SR_CLIENT_ID/roles/ResourceOwner/bindings -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"scope":{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}, "resourcePatterns":[{"resourceType":"Topic", "name":"_exporter_states", "patternType":"LITERAL"}]}'
}

_assign_c3_role_bindings() {
  curl -X --insecure POST $1/User:$C3_CLIENT_ID/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'
  curl -X --insecure POST $1/User:$C3_CLIENT_ID/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q", "schema-registry-cluster":"schema-registry-cluster"}}'
  curl -X --insecure POST $1/User:$C3_CLIENT_ID/roles/ResourceOwner/bindings -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"scope":{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}, "resourcePatterns":[{"resourceType":"Topic", "name":"_confluent-command", "patternType":"LITERAL"}]}'
}

_assign_client_role_bindings() {
  curl -X --insecure POST $1/User:$CLIENT_APP_ID/roles/ResourceOwner/bindings -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"scope":{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}, "resourcePatterns":[{"resourceType":"Group", "name":"schema-registry-group", "patternType":"LITERAL"}]}'
}

_assign_users_role_bindings(){
  curl -X --insecure POST $1/Group:$SSO_SUPER_USER_GROUP/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'
  curl -X --insecure POST $1/Group:$SSO_SUPER_USER_GROUP/roles/SystemAdmin -H "Authorization: Bearer $2" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q", "schema-registry-cluster":"schema-registry-cluster"}}'
}

# Assign application role bindings
assign_role_bindings() {
  auth_token=$(curl -s -d "client_id=$SUPERUSER_CLIENT_ID" -d "client_secret=$SUPERUSER_CLIENT_SECRET" -d "grant_type=client_credentials" $IDP_TOKEN_ENDPOINT | grep -Po '"access_token": *\K"[^"]*"' | grep -o '[^"]*')
  echo "$auth_token"

  MDS_RBAC_ENDPOINT=https://broker1:8091/security/1.0/principals

  _assign_sr_role_bindings  $MDS_RBAC_ENDPOINT $auth_token
  _assign_c3_role_bindings $MDS_RBAC_ENDPOINT $auth_token
  _assign_client_role_bindings $MDS_RBAC_ENDPOINT $auth_token
  _assign_users_role_bindings $MDS_RBAC_ENDPOINT $auth_token
}

create_topic() {
  kafka-topics --bootstrap-server broker1:59092 --topic $1 --create --command-config /etc/confluent/configs/superuser.properties
}

# Run it
assign_role_bindings

#curl -s -d "client_id=superuser_client_app" -d "client_secret=superuser_client_app_secret" -d "grant_type=client_credentials" http://keycloak:8080/realms/cp/protocol/openid-connect/token
#curl -X POST https://localhost:8091/security/1.0/principals/User:sr_client_app/roles/SystemAdmin -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlOFQ0Q3c3SmdSQW4zWWFpZU41NXptQngxaDYwN3J2Q041Qm5xNzNhUWZVIn0.eyJleHAiOjE3NDk0MDQ5NDMsImlhdCI6MTc0OTQwMTM0MywianRpIjoidHJydGNjOmI5ODA4NzQzLWNjN2UtNDMwNS1iMjFhLTFkMjk3NjZmNjcyYyIsImlzcyI6Imh0dHA6Ly9rZXljbG9hazo4MDgwL3JlYWxtcy9jcCIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJzdXBlcnVzZXJfY2xpZW50X2FwcCIsInR5cCI6IkJlYXJlciIsImF6cCI6InN1cGVydXNlcl9jbGllbnRfYXBwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtY3AiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7IiR7Y2xpZW50SWR9Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJjbGllbnRJZCI6InN1cGVydXNlcl9jbGllbnRfYXBwIiwiY2xpZW50SG9zdCI6IjE3Mi4xOC4wLjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImdyb3VwcyI6WyIvYXBwX2dyb3VwMSIsIi9zdXBlcl91c2VycyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtc3VwZXJ1c2VyX2NsaWVudF9hcHAiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjE4LjAuMSJ9.Rgh8QoHAdB6gq4fEk9b2E72pAon6OztlEnM9jhxP07j40Pg9Fp-ve0RsLIblH2U-veQX-cv9i2ggJtWauX21hyIkOLeQ_IQvuB4moqdThE3vVl5_V2ZQjwKsTXMOQ6xUCblGJqF0cXrInO_fRKHOX3jWFsKzzotl2yXd3wwqibCPhOE8Z4K4wc7lQPhzHBEaraFA5UbVL1UIolRCXIIDqy2J3k8EeLooeumFhebXWnDFD_HhRJUmF04cQQNDDsl-Y4Z7Vaj8aBYVPtCaHRQ5bC1rqN2JafY-XF49GgfoXfwp3vT5gJ-2hh_jE6tpEhrEHZ6WgwGIsVJHjKgpmEC4dQ" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'