gradle wrapper --gradle-version 8.13
./gradlew clean build -x test
./gradlew :adapter:quarkusDev

psql -h localhost -p 5432 -U ldnhan -d user

docker compose up -d
docker compose down -v
docker logs postgres
docker stop postgres
docker rm -f -v postgres

/opt/keycloak/bin/kc.sh export \
--dir /opt/keycloak/data/export \
--realm quarkus \
--users realm_file

docker inspect keycloak | jq '.[0].State.Health.Log[] | {ExitCode, Output}'

sudo find . -name '*certs*' -exec sudo chattr -i {} \;
sudo find . -name '*certs*' -exec sudo chown -R $USER:$USER {} \;
sudo find . -name '*certs*' -exec sudo chmod -R u+rw {} \;
sudo find . -name '*keypair*' -exec sudo chattr -i {} \;
sudo find . -name '*keypair*' -exec sudo chown -R $USER:$USER {} \;
sudo find . -name '*keypair*' -exec sudo chmod -R u+rw {} \;

bash
sudo ./start.sh
sudo ./stop.sh

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




