#pragma CIVL ACSL
int x;
int * a;
int (*b)[10];

/*@ assigns (a + (0 .. 9))[0 .. 9];
  @ assigns (a + (0 .. 9))[x + (0 .. 9)];
  @ assigns a[x + (0 .. 9)];
  @ assigns (a + (0 .. 9))[x];
  @ assigns (b + (0 .. 9))[0 .. 9][0 .. 9];
  @*/
int f() {
  return 0;
}
