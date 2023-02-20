#pragma CIVL ACSL
int *a;
int x;

/*@
  @ assigns x + *(a + (0 .. 10));
  @*/
int f() {
  return 0;
}
