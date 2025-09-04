#!/bin/bash

docker stop $(docker ps -a -q) || true
docker rm $(docker ps -a -q) || true

docker run --name rococo-all -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=secret \
  -v mysqldata:/var/lib/mysql \
  -v ./mysql/init:/docker-entrypoint-initdb.d \
  -e TZ=GMT+3 -d mysql:8.0

docker run --name kafka -p 9092:9092 -p 9093:9093 -d \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092,CONTROLLER://localhost:9093 \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  apache/kafka:3.4.0