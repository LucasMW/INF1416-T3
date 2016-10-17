run: build main.db
	sh scripts/run.sh Main
logView: build
	sh scripts/run.sh LogView
build:
	sh scripts/build.sh
main.db:
	sqlite3 $@ -init setup.sql '.exit'
clean:
	rm -rf bin/
	mkdir bin
cleandb:
	rm main.db