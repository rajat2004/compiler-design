all: P5

P5: src/visitor/generateRA.java
	rm -rf CS17B042
	rm CS17B042.P5.tar.gz
	mkdir -p CS17B042/P5
	cp -avr src/* CS17B042/P5/
	cp ChecklistP5.txt CS17B042/
	tar -czvf CS17B042.P5.tar.gz CS17B042
