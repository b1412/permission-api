DIR := ${CURDIR}

#if make is typed with no further arguments, then show a list of available targets
default:
	@awk -F\: '/^[a-z_]+:/ && !/default/ {printf "- %-20s %s\n", $$1, $$2}' Makefile

help:
	@echo ""
	@echo "make up: start all containers"
	@echo "make stop [CONTAINER]: stop [CONTAINER]"
	@echo "make logs [CONTAINER]: get logs"
	@echo "make down: do a docker-compose down -v"
	@echo "make start [CONTAINER]: start [CONTAINER]"
	@echo ""

up:
	docker-compose up -d $(filter-out $@,$(MAKECMDGOALS))

pull:
	docker-compose pull

down:
	docker-compose down $(filter-out $@,$(MAKECMDGOALS))

start:
	docker-compose start $(filter-out $@,$(MAKECMDGOALS))

stop:
	docker-compose stop $(filter-out $@,$(MAKECMDGOALS))

logs:
	docker-compose logs -f --tail=200 $(filter-out $@,$(MAKECMDGOALS))

prune:
	@echo "pruning some old containers and images"
	docker container prune --filter "until=336h"
	docker image prune -a

dance:
	$(MAKE) stop
	$(MAKE) prune
	$(MAKE) pull
	docker-compose down -v
	@echo "You may now run docker-compose up"

%:
	@:
