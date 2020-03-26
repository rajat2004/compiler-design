all: P1

P1: P1.l P1.y
	cp P1.l ./CS17B042/P1.l
	cp P1.y ./CS17B042/P1.y
	tar -czvf CS17B042.P1.tar.gz CS17B042
	bison -d P1.y
	flex P1.l
	gcc P1.tab.c lex.yy.c -lfl -o P1

clean:
	rm P1.tab.* lex.yy.c P1
