#!/bin/bash
source ./docker.properties

# Устанавливаем профиль для сборки и префикс образа
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

echo "Building docker image for geo module..."
./gradlew :rococo-geo:jibDockerBuild

echo "Stopping and removing old geo.rococo.dc container if exists..."
docker stop geo.rococo.dc 2>/dev/null || true
docker rm geo.rococo.dc 2>/dev/null || true

echo "Starting geo.rococo.dc container..."
docker compose up -d geo.rococo.dc

echo "Current geo.rococo.dc container status:"
docker ps -a --filter "name=geo.rococo.dc"
