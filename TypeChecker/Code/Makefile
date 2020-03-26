all: P2

P2: src/visitor/TypeChecker.java
#	cd src && javac P2.java && cd ..
	rm -rf CS17B042
	rm CS17B042.P2.tar.gz
	mkdir -p CS17B042/P2
	cp -avr src/* CS17B042/P2/
	cp ChecklistP2.txt CS17B042/
# 	cp P1.l ./CS17B042/P1.l
# 	cp P1.y ./CS17B042/P1.y
	tar -czvf CS17B042.P2.tar.gz CS17B042
# 	bison -d P1.y
# 	flex P1.l
# 	gcc P1.tab.c lex.yy.c -lfl -o P1
