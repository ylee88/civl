#pragma CIVL ACSL
int * a;

/*@ 
  @ assigns *(*(a + (0 .. 10)) + (0 .. 10));
  @*/
int f() {
  return 0;
}
