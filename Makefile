run: build main.db
	sh scripts/run.sh Main
build:
	sh scripts/build.sh
main.db:
	sqlite3 $@ -init setup.sql '.exit'
clean:
	rm -rf bin/
	mkdir bin
