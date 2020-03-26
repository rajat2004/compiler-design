all: P4

P4: src/visitor/microIR_gen.java
	rm -rf CS17B042
	rm CS17B042.P4.tar.gz
	mkdir -p CS17B042/P4
	cp -avr src/* CS17B042/P4/
	cp ChecklistP4.txt CS17B042/
	tar -czvf CS17B042.P4.tar.gz CS17B042
