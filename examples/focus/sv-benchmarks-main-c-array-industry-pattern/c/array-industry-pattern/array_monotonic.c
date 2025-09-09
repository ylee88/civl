/* Verified with mods:
 * for loop step size of 2 changed to step of 1
 */
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#pragma CIVL ACSL
$input int N;
$assume(0 < N);

int __VERIFIER_nondet_int() {
  int x;
  $havoc(&x);
  return x;
}
int main()
{
	int i;
	int a[N];
	int b[N];

  // modified:
  //@ focus I;
  for(i = 0; i < N; i++)
	{
		a[i] = __VERIFIER_nondet_int();
		if(a[i] == 10)
			b[i] = 20;
	}
  /* original:
	for(i = 0; i < N;  i = i + 2)
	{
		a[i] = __VERIFIER_nondet_int();
		if(a[i] == 10)
			b[i] = 20;
	}
  */

  // modified:
  //@ focus I;
  $assert($forall(int i:0..N-1) a[i] == 10 => b[i] == 20);
  /* original:
	for(i = 0; i < N; i = i + 2)
	{
		if(a[i] == 10)
			$assert(b[i] == 20);
	}
  */
}

