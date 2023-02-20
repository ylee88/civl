#pragma CIVL ACSL

struct T {
  int x[100];
} * a;

/*@ assigns a->x;
  @*/
int f() {
  return 0;
}
