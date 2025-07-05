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
/opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/export --realm kafka --users realm_file

docker inspect broker1 | jq '.[0].State.Health.Log[] | {ExitCode, Output}'
```

```shell
curl --insecure -X POST https://localhost:8443/realms/quarkus/protocol/openid-connect/token \
--user "backend-service:secret" \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'username=admin&password=admin&grant_type=password&scope=basic'
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
-keystore /home/ben/Downloads/clean-architecture-quarkus/docker/kafka/broker1/certs/truststore.p12 \
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
auth_token=$(curl -s -d "client_id=superuser_client_app" \
                  -d "client_secret=superuser_client_app_secret" \
                  -d "grant_type=client_credentials" \
                  --insecure "https://keycloak:8443/realms/cp/protocol/openid-connect/token" | \
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
