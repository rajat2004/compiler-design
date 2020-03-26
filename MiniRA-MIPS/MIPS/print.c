#include <stdio.h>

void print_int(int i)
{
	printf("%d", i);
}

void print_char(int c)
{
	putchar(c);
}

void foo(int j)
{
	int i = 1;
	while (i <= j) {
		print_int(i);
		print_char(' ');
		i++;
	}
	print_char('\n');
}

int main(void)
{
	foo(10);
	return 0;
}
