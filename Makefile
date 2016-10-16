run: build
	sh scripts/run.sh Main
build:
	sh scripts/build.sh
clean:
	rm -rf bin/
	mkdir bin
