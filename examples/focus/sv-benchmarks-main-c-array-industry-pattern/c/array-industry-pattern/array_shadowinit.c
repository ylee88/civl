/* Verified with mods and invariants:
 * Changed while loops to for loops
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
int __VERIFIER_nondet_int(void) {
  int x;
  $havoc(&x);
  return x;
}
$input int N;
/*
  The program initializes the array 'a' in a loop having loop counter 'i' with a variable 'k' which is a shadow of 'i'. Checks universally quantified property \forall i, a[i] = i.
*/

int main() {
 if(N>0) {
  int i,k;
  int a[N];

  // modified:
  k = 0;
  //@ transform flatten i;
  /*@ loop invariant 0 <= i && i <= N;
    @ loop invariant i == k;
    @*/
  for (i = 0; i < N; i++) {
    a[k] = k;
    k = k+1;
  }
  /* original:
  i=0;
  k=0;
  while(i < N) {
   a[k]=k;
   i=i+1;
   k=k+1;
  }
  */

  // modified:
  //@ transform flatten i;
  $assert($forall(int i:0..N-1) a[i] == i);
  /* original:
  i=0;
  while(i < N) {
   __VERIFIER_assert(a[i]==i);
   i=i+1;
  }
  */
 }
 return 0;
}
