#pragma CIVL ACSL
int * a;
double x[10];

/*@ assigns (a + (0 .. 9))[x[0 .. 9]];
  @*/
int f() {
  return 0;
}
