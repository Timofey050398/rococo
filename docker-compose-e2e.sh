#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export JDBC_HOST=rococo-all-db
export ARCH=$(uname -m)

# --- Аргументы ---
MODE=$1               # test-only | пусто
BROWSER_ARG=$2        # firefox | пусто → chrome


if [ "$MODE" != "test-only" ]; then
  echo "### Running in FULL mode ###"
  docker compose down

  docker_containers=$(docker ps -a -q)
  if [ ! -z "$docker_containers" ]; then
    echo "### Stop containers: $docker_containers ###"
    docker stop $docker_containers
    docker rm $docker_containers
  fi
else
  echo "### Running in TEST-ONLY mode ###"
  autotest_container=$(docker ps -a -q --filter "ancestor=${PREFIX}/rococo-autotest:latest")
  if [ ! -z "$autotest_container" ]; then
    echo "### Stop only autotest container: $autotest_container ###"
    docker stop $autotest_container
    docker rm $autotest_container
  fi

  echo "### Rebuilding e2e tests image ###"
  ./gradlew :rococo-autotest:clean
  docker build -t ${PREFIX}/rococo-autotest:latest -f ./rococo-autotest/Dockerfile .

  echo "### Starting test profile containers ###"
  docker compose --profile test up -d
  docker ps -a
  exit 0
fi

# --- Браузер ---


if [ "$BROWSER_ARG" = "firefox" ]; then
  export BROWSER="firefox"
  docker pull selenoid/vnc_firefox:125.0
else
  export BROWSER="chrome"
  docker pull selenoid/vnc_chrome:127.0
fi


# Проверяем образы, если чего-то нет → пересобираем всё, автотесты не через jib
for image in \
  "mysql:8.0" \
  "confluentinc/cp-zookeeper:7.3.2" \
  "confluentinc/cp-kafka:7.3.2" \
  "${PREFIX}/rococo-auth-docker:latest" \
  "${PREFIX}/rococo-artist-docker:latest" \
  "${PREFIX}/rococo-gateway-docker:latest" \
  "${PREFIX}/rococo-geo-docker:latest" \
  "${PREFIX}/rococo-museum-docker:latest" \
  "${PREFIX}/rococo-painting-docker:latest" \
  "${PREFIX}/rococo-userdata-docker:latest" \
  "${PREFIX}/rococo-grpc-docker:latest" \
  "${PREFIX}/rococo-client-docker:latest" \
  "${PREFIX}/rococo-kafka-log-docker:latest" \
  "aerokube/selenoid:1.11.3" \
  "aerokube/selenoid-ui:1.10.11" \
  "${PREFIX}/rococo-autotest:latest" \
  "frankescobar/allure-docker-service:2.27.0" \
  "frankescobar/allure-docker-service-ui:7.0.3"; do

  if [[ "$(docker images -q "$image" 2> /dev/null)" == "" ]]; then
    echo "### image $image doesn't exist locally ###"
    echo "### Building images ###"
    java --version
    ./gradlew clean
    ./gradlew jibDockerBuild -x :rococo-autotest:test
    break 2
  fi
done

# Поднимаем всё окружение
echo "### Starting full environment ###"
docker compose up -d

docker ps -a