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
/opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/export --realm quarkus --users realm_file

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
