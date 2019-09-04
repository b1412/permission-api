#!/usr/bin/env bash
./gradlew clean build -x test
docker build -t cannon .
