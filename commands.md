```shell
gradle wrapper --gradle-version 8.13
./gradlew clean build -x test
./gradlew :adapter:quarkusDev
```

```shell
psql -h localhost -p 5432 -U ldnhan -d user
select * from concert;
```

```shell
docker compose up -d
docker compose down -v
docker logs -f postgres
docker stop postgres
docker rm -f -v postgres

docker exec -it keycloak /bin/bash 
/opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/export --realm concert-global --users realm_file

docker inspect mongodb | jq '.[0].State.Health.Log[] | {ExitCode, Output}'
```

```shell
curl --insecure -X POST https://localhost:8443/realms/concert-global/protocol/openid-connect/token \
--user "concert-service:ggj3eeGCjmgFsk8Yob5l3PiJ4hieDyod" \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'username=3121410359@sv.sgu.edu.vn&password=123&grant_type=password'
```

```shell
sudo find . -name '*certs*' -exec sudo chattr -i {} \;
sudo find . -name '*certs*' -exec sudo chown -R $USER:$USER {} \;
sudo find . -name '*certs*' -exec sudo chmod -R u+rw {} \;
sudo find . -name '*keypair*' -exec sudo chattr -i {} \;
sudo find . -name '*keypair*' -exec sudo chown -R $USER:$USER {} \;
sudo find . -name '*keypair*' -exec sudo chmod -R u+rw {} \;

bash
source generate_ca.sh
docker compose -f ../kafka-cluster.yml up -d
docker compose -f ../kafka-cluster.yml down -v
sudo ./start.sh
sudo ./stop.sh
```

```shell
keytool -list \
-keystore /home/ben/Downloads/clean-architecture-quarkus/docker/keycloak/certs/truststore.p12 \
-storetype PKCS12 \
-storepass 120103

keytool -list \
-keystore /home/ben/Projects/clean-architecture-quarkus/docker/keycloak/certs/truststore.p12 \
-storetype PKCS12 \
-storepass 120103

# Extract cert từ truststore2 để kiểm tra
keytool -exportcert \
-keystore /home/ben/Downloads/clean-architecture-quarkus/docker/keycloak/certs/truststore.p12 \
-storetype PKCS12 \
-storepass 120103 \
-alias myca \
-rfc \
-file ca2.pem

# Dùng openssl verify với truststore1 làm CA
keytool -exportcert \
-keystore /home/ben/Downloads/clean-architecture-quarkus/docker/kafka/broker1/certs/truststore.p12 \
-storetype PKCS12 \
-storepass 120103 \
-alias myca \
-rfc \
-file ca1.pem

# Giờ verify cert từ truststore2 bằng CA1
openssl verify -CAfile ca1.pem ca2.pem
```

```shell
auth_token=$(curl -s -d "client_id=kafka" \
                  -d "client_secret=8bNftAs6uLqtcamZZltk1IrGnjMxvl9k" \
                  -d "grant_type=client_credentials" \
                  --insecure "https://keycloak:8443/realms/kafka/protocol/openid-connect/token" | \
             grep -Po '"access_token": *\K"[^"]*"' | grep -o '[^"]*')

echo "Access Token: $auth_token"
```

curl --insecure -s -d "client_id=superuser_client_app" -d "client_secret=superuser_client_app_secret" -d "grant_type=client_credentials" "https://localhost:8443/realms/cp/protocol/openid-connect/token"


sudo tar -czvf clean-architecture-quarkus.tar.gz clean-architecture-quarkus


kafka1:
image: apache/kafka:4.0.0  #  natives Apache-Image
container_name: kafka1
hostname: kafka1
networks: [kafka-net]
ports: ["19092:19092"]  #  OAUTH Listener
volumes:
- kafka1-data:/var/lib/kafka
- ./libs/kafka-oauth-common-0.16.2.jar:/opt/kafka/libs/kafka-oauth-common-0.16.2.jar:ro
- ./libs/kafka-oauth-server-0.16.2.jar:/opt/kafka/libs/kafka-oauth-server-0.16.2.jar:ro
- ./libs/kafka-oauth-client-0.16.2.jar:/opt/kafka/libs/kafka-oauth-client-0.16.2.jar:ro
- ./kafka_broker_jaas.conf:/opt/kafka/config/kafka_broker_jaas.conf:ro
- ./log4j2.properties:/opt/kafka/config/log4j2.properties:ro

    environment:
      CLUSTER_ID: "ac12e3f4-5678-91ab-cdef-1334567890ab"
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: "broker,controller"
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka1:9093,2@kafka2:9093,3@kafka3:9093"

      KAFKA_LISTENERS: "OAUTH://0.0.0.0:19092,CONTROLLER://0.0.0.0:9093"
      KAFKA_ADVERTISED_LISTENERS: "OAUTH://kafka1:19092"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "OAUTH:SASL_PLAINTEXT,CONTROLLER:PLAINTEXT"
      KAFKA_CONTROLLER_LISTENER_NAMES: "CONTROLLER"
      KAFKA_INTER_BROKER_LISTENER_NAME: "OAUTH"
      KAFKA_PRINCIPAL_BUILDER_CLASS: "io.strimzi.kafka.oauth.server.OAuthKafkaPrincipalBuilder"
      KAFKA_SASL_ENABLED_MECHANISMS: "OAUTHBEARER"
      KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL: "OAUTHBEARER"
      KAFKA_AUTHORIZER_CLASS_NAME: ""
      KAFKA_LOG4J_OPTS: "-Dlog4j.configurationFile=/opt/kafka/config/log4j2.properties"

      KAFKA_OPTS: |
        -Xlog:class+path=info
        -Djava.security.auth.login.config=/opt/kafka/config/kafka_broker_jaas.conf
        -Dlistener.name.oauth.oauthbearer.sasl.server.callback.handler.class=io.strimzi.kafka.oauth.server.OAuthValidatorCallbackHandler
        -Dlistener.name.oauth.oauthbearer.sasl.login.callback.handler.class=io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler
        -Doauth.jwks.endpoint.uri=http://keycloak:8080/realms/kafka/protocol/openid-connect/certs
        -Doauth.valid.issuer.uri=http://keycloak:8080/realms/kafka
        -Doauth.username.claim=sub
        -Dlistener.name.oauth.oauthbearer.token.endpoint.url=http://keycloak:8080/realms/kafka/protocol/openid-connect/token
        -Dlistener.name.oauth.oauthbearer.jwks.endpoint.url=http://keycloak:8080/realms/kafka/protocol/openid-connect/certs
        -Dlistener.name.oauth.oauthbearer.expected.issuer=http://keycloak:8080/realms/kafka
        -Dlistener.name.oauth.oauthbearer.username.claim=sub


curl --insecure https://localhost:8081/subject --cert /certs/cert.pem --key /certs/key.pem --cacert /certs/ca.crt

curl -d "client_id=superuser_client_app" -d "client_secret=superuser_client_app_secret" -d "grant_type=client_credentials" --insecure https://localhost:8443/realms/cp/protocol/openid-connect/token

curl -X POST --insecure https://localhost:8091/security/1.0/principals -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlOFQ0Q3c3SmdSQW4zWWFpZU41NXptQngxaDYwN3J2Q041Qm5xNzNhUWZVIn0.eyJleHAiOjE3NTE4NzcwNjIsImlhdCI6MTc1MTg3MzQ2MiwianRpIjoidHJydGNjOmY3NGU4MTgxLTk4ZTctNGU3Ni04OGVkLTY0NTM3NmI0YzIzZiIsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0Ojg0NDMvcmVhbG1zL2NwIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6InN1cGVydXNlcl9jbGllbnRfYXBwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3VwZXJ1c2VyX2NsaWVudF9hcHAiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1jcCIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiJHtjbGllbnRJZH0iOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImNsaWVudElkIjoic3VwZXJ1c2VyX2NsaWVudF9hcHAiLCJjbGllbnRIb3N0IjoiMTcyLjE4LjAuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZ3JvdXBzIjpbIi9hcHBfZ3JvdXAxIiwiL3N1cGVyX3VzZXJzIl0sInByZWZlcnJlZF91c2VybmFtZSI6InNlcnZpY2UtYWNjb3VudC1zdXBlcnVzZXJfY2xpZW50X2FwcCIsImNsaWVudEFkZHJlc3MiOiIxNzIuMTguMC4xIn0.l9wTut5RFuFDtCMgYJmmtvi-ZRKR628WP-AoTj7fwPokkY1wKHOFbz10XWrAeyuhRFfACng8jkai331QvkY_jwZb3M-4nXnlTXmPnX7QP_uao_2CSoD8q7B8ilCQg_Ogbi_Rp1gRkVLTibwLBc40NAAyx0_NQQ0rUrezY6mumYAkUjNemxq2u2HlZe525UwBSZX8dW3evdyJFjlnqr_wuH8P6qGiHqHxqTdV_6Gw7YNhIMfMf3aAJqZpjzSetwwYVYEWBbfcIdOL_SSqmspaiu11IgBv05TuXzzsagnCQgle9eUpWaaNqhJfI_PNI8xXF0eL3Ji_lkdOBd77m71QkA" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'


curl -X POST --insecure https://localhost:8091/security/1.0/principals/User:service-account-kafka/roles/SystemAdmin -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1dHBLbDZRSEpoM1RhZk9JOWJRWWZPbW82ZmpPYWZ0MXNyUUUzT2JUeVdzIn0.eyJleHAiOjE3NTE4NjEwNDcsImlhdCI6MTc1MTg2MDc0NywianRpIjoidHJydGNjOmI4NGE4OGIwLTNjMWEtNDZkZC05MzIwLTA0NTFkYzZlZTA5MyIsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0Ojg0NDMvcmVhbG1zL2thZmthIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImthZmthIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoia2Fma2EiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWthZmthIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SG9zdCI6IjE3Mi4xOC4wLjEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQta2Fma2EiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjE4LjAuMSIsImNsaWVudF9pZCI6ImthZmthIn0.kp8ljvu0xgAJ1BkncZajqTftXCOjfFWFtcVi7GBvHHiDIfMU8xd6h850XrKJAq4JAvtDlXzDWsb-cnSBjHOZJjEyY73s2u-V618GXOfTwY7adZOZRUqW_6I0aSZF8pndMp5u3SwoXZRymN_KQXEvT9Y_RL1TnSPVcRZHfTqKLMENxEQWOK-3pDPRuKo3qGs8ujZw8yxR4j9bEMV-4ZQrzk4_s67jhbCCYa_WQfk4ntVjuE-5Cmddg4X_Yul2RYfHdTFlxTmXewrYHXcxk-FD1_5WDzOgDe5ufKH18niwbE5fzPthSXJ5H4WGRpiRPNt8aQmVud5zLslsHvpHJoIlQA" -i -H "Content-Type: application/json" -H "Accept: application/json" -d '{"clusters":{"kafka-cluster":"vHCgQyIrRHG8Jv27qI2h3Q"}}'


kafka-topics --bootstrap-server localhost:19091 --describe --topic quote-requests
