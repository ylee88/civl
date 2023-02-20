#pragma CIVL ACSL
int * a;

/*@ assigns *(a + (0 .. 9));
  @ assigns *(a + (1 + (0 .. 9)));
  @ assigns *(a + ((1 .. 9) + (0 .. 9)));
  @ assigns *(((1 .. 9) + (0 .. 9)) + a);
  @*/
int f() {
  return 0;
}
