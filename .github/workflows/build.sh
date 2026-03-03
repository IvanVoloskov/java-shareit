cp -rf ./tests/checkstyle.xml ./checkstyle.xml
cp -rf ./tests/suppressions.xml ./suppressions.xml
cp -rf ./tests/Dockerfile ./Dockerfile
cp -rf ./tests/docker-compose.yml ./docker-compose.yml

docker compose -f docker-compose.yml build

docker compose -f docker-compose.yml up -d

sleep 30

curl -f http://localhost:8080/actuator/health || exit 1

mvn enforcer:enforce -Denforcer.rules=requireProfileIdsExist -P check --no-transfer-progress &&
mvn verify -P check --no-transfer-progress

docker compose -f docker-compose.yml down