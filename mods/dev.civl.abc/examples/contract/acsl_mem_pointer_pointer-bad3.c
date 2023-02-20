#pragma CIVL ACSL
int * a;
double x[10];
/*@ 
  @ assigns *(a + x[0 .. 9]);
  @*/
int f() {
  return 0;
}
