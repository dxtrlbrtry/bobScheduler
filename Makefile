docker-build:
	docker build -t bob-kt .
docker-run-debug:
	docker run --rm -it --entrypoint="" -v $(PWD):/app bob-kt /bin/bash
docker-run:
	docker run --rm -e SCHEDULE_LENGTH=${SCHEDULE_LENGTH} -v $(PWD)/releases.txt:/app/releases.txt -v $(PWD)/artifacts:/app/artifacts bob-kt
build:
	mvn clean package
run:
	java -classpath $(PWD)/target/classes:$(PWD)/target/lib/kotlin-stdlib.jar org.main.MainKt
