#pragma CIVL ACSL
struct T {
  int (*x)[100];
} ** a;

struct T (*b)[10];

/*@ assigns a[0..10][0..10].x[0 .. 10][0 .. 10];
  @ assigns (*(a + (0 .. 10)))->x[0 .. 10][0 .. 10];
  @ assigns b[0 .. 10][0].x[0][0 .. 10]; 
  */
int f() {
  return 0;
}
