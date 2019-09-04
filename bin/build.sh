#!/usr/bin/env bash
gradle clean build -x test
docker build -t cannon .
