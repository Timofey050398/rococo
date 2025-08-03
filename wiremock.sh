#!/bin/bash

# Получаем абсолютный путь к корню проекта (где лежит wiremock/rest)
# Для Git Bash на Windows преобразуем путь в формат, понятный Docker

if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
  # Преобразуем путь из /c/Users/... в C:/Users/...
  WIN_PATH=$(pwd | sed 's/^\/\([a-z]\)\/\(.*\)/\U\1:\//; s/\//\\/g')
  HOST_PATH="$WIN_PATH\\wiremock\\rest"
else
  # Для Linux/macOS
  HOST_PATH="$(pwd)/wiremock/rest"
fi

docker run --name rococo-mock \
  -p 8080:8080 \
  -v "$HOST_PATH:/home/wiremock" \
  -d wiremock/wiremock:2.35.0 \
  --global-response-templating \
  --enable-stub-cors
