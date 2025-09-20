#!/bin/bash
set -e

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

# --- Браузер ---
if [ "$BROWSER_ARG" = "firefox" ]; then
  export BROWSER="firefox"
  docker pull selenoid/vnc_firefox:125.0
else
  export BROWSER="chrome"
  docker pull selenoid/vnc_chrome:127.0
fi

# --- TEST-ONLY режим ---
if [ "$MODE" = "test-only" ]; then
  echo "### Running in TEST-ONLY mode ###"

  autotest_container=$(docker ps -a -q --filter "ancestor=${PREFIX}/rococo-autotest:latest")
  if [ -n "$autotest_container" ]; then
    echo "### Stop old autotest container: $autotest_container ###"
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

# --- FULL режим ---
echo "### Running in FULL mode ###"
docker compose down

docker_containers=$(docker ps -a -q)
if [ -n "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

# список внешних образов, которые должны тянуться с DockerHub
external_images=(
  "mysql:8.0"
  "confluentinc/cp-zookeeper:7.3.2"
  "confluentinc/cp-kafka:7.3.2"
  "aerokube/selenoid:1.11.3"
  "aerokube/selenoid-ui:1.10.11"
  "frankescobar/allure-docker-service:2.27.0"
  "frankescobar/allure-docker-service-ui:7.0.3"
)

# проверяем внешние
for image in "${external_images[@]}"; do
  if [[ -z "$(docker images -q "$image" 2>/dev/null)" ]]; then
    echo "### pulling $image ###"
    docker pull "$image"
  fi
done

# билдим java-сервисы
./gradlew clean
./gradlew jibDockerBuild -x :rococo-autotest:test

# фронтенд всегда пересобираем
echo "### Building frontend image (rococo-client-docker) ###"
docker build \
  -t ${PREFIX}/rococo-client-docker:latest \
  --build-arg NPM_COMMAND=build:docker \
  -f ./rococo-client/Dockerfile \
  ./rococo-client

# пересобираем e2e
echo "### Building autotest image (rococo-autotest) ###"
./gradlew :rococo-autotest:clean
docker build -t ${PREFIX}/rococo-autotest:latest -f ./rococo-autotest/Dockerfile .

# Поднимаем всё окружение
echo "### Starting full environment ###"
docker compose up -d

docker ps -a