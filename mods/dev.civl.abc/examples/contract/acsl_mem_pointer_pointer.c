#pragma CIVL ACSL
int ** a;
int x;
int y[10];

/*@ assigns a[0 .. 10][0];
  @ assigns a[0][0 .. 10];
  @ assigns *(a[0 .. 10] + (0 .. 10));
  @ assigns *(*(a + (0 .. 10)) + (0 .. 10));
  @ assigns *(*(a + (0 .. 10)) + 1);
  @ assigns *(*(a + 1) + (0 .. 10));
  @ assigns *(*(a + 1) + (x + (0 .. 10)));
  @ assigns *(a + (y[0 .. 9] + 1));
  @*/
int f() {
  return 0;
}
