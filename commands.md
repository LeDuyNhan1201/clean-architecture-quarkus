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


