.PHONY: default

default:
	@echo "Happy hacking!"

test:
	mvn test

test-unit:
	mvn test -Dtest=*UnitTest -DfailIfNoTests=false

test-integration:
	mvn test -Dtest=*IntegrationTest -DfailIfNoTests=false

test-e2e:
	docker-compose \
		-f ../../docker-compose.prod.yaml \
		-f ../../docker-compose.dev.yaml \
		-f ../../docker-compose.yaml \
		run -it -v $(shell pwd):/source -w /source post-service \
		mvn test -Dtest=*E2ETest -DfailIfNoTests=false
#	docker \
#		run -it -v $(shell pwd):/source -w /source --net habsat-starter_habsat_network maven:3.8.6-jdk-11 \
#		mvn test -Dtest=*E2ETest -DfailIfNoTests=false

