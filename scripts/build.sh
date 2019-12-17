#!/usr/bin/env bash
./gradlew  build -x test
docker build -t kotlin-cannon-api .
