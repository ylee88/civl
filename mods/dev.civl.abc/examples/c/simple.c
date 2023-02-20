#include<stdio.h>
int x = 1;
int y = 0;
int a[3];
int b[5][5];

int main() {
	printf("%d\n", x);
	int z;
/*
	//Part 2: Array
	x = 7;
	a[0] = 1;
	a[1] = 6;

	x = x * 2;
	a[2] = 3;
	x = x - 2;
	b[4][4] = 10;
	b[4][2] = 13;
	b[a[1]][a[2]] = 15;
	b[a[1]][2] = 2*a[a[a[0]]];
*/

//	x = 1;
//	y = 2;

	//Part 3: If condition
//	if(x > y + 2)
//		z = 1;
//	else
//		z = 2;
	a[0] = 1;
	a[1] = 5;

	b[1][1] = 2;
	b[a[1]][2] = 4;

	if (a[b[2][1]] > b[1][0] + 2)
		z = 1;
	else
		z = 2;

	x = 1;
/*
	for(int i = 0; i < 5; i++){
		x = x + 1;
	}
*/
}
