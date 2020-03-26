all: P3

P3: src/visitor/IR_generator.java
	rm -rf CS17B042
	rm CS17B042.P3.tar.gz
	mkdir -p CS17B042/P3
	cp -avr src/* CS17B042/P3/
	cp ChecklistP3.txt CS17B042/
# 	cp P1.l ./CS17B042/P1.l
# 	cp P1.y ./CS17B042/P1.y
	tar -czvf CS17B042.P3.tar.gz CS17B042
# 	bison -d P1.y
# 	flex P1.l
# 	gcc P1.tab.c lex.yy.c -lfl -o P1
