/*@ predicate
  @   IsSubArray{S}(int * a, integer offset, int * b, integer m) = 
  @     m >= 0 && (\forall integer i; 0 <= i < m ==> a[offset + i] == b[i]);
  @*/

/*@ requires \valid(a + (0 .. m-1)) && \valid(b + (0 .. m-1));
  @ requires m >= 0;
  @ assigns \nothing;
  @ ensures \result <==> IsSubArray{Here}(a, 0, b, m);
  @*/
_Bool equals(int *a, int * b, int m) {
  /*@ loop invariant 0 <= i <= m;
    @ loop invariant IsSubArray(a, 0, b, i);
    @ loop assigns i;
    @ loop variant m - i;
    @*/
  for (int i = 0; i < m; i++)
    if (a[i] != b[i]) 
      return 0;
  return 1;
}

/*@ requires \valid(a + (0 .. n-1)) && \valid(b + (0 .. m-1));
  @ requires n >= 0 && m >= 0;
  @ assigns  \nothing;
  @ ensures  \result != n ==> IsSubArray{Here}(a, \result, b, m);
  @ ensures  (\forall integer i1; 0 <= i1 <= n-m ==> !IsSubArray{Here}(a, i1, b, m)) ==> \result == n;
  @ ensures  0 <= \result <= n;
  @*/
int search(int *a, int n, int *b, int m) {
  int i;

  if (n < m) 
    return n;
  /*@ loop invariant 0 <= i <= n-m+1;
    @ loop invariant \forall integer i1; 0 <= i1 < i ==> !IsSubArray{Here}(a, i1, b, m);
    @ loop assigns i;
    @ loop variant n - m - i;
    @*/
  for (i = 0; i <= n - m; i++)
    if (equals(a+i, b, m))
      return i;
  return n;
}
