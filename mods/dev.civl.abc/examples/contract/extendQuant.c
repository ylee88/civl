#pragma CIVL ACSL
/*@ ensures \result == x-1; */
int g(int x);

/*@ ensures \result==\sum(x,y, \lambda int i; 2*i);
  @*/
int f(int x, int y);
