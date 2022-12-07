/* Verifiable!
 */
#include <stdlib.h>
#include <assert.h>

#pragma CIVL ACSL

$input int N;
$assume(0 < N);
short __VERIFIER_nondet_short() {
  short x;
  $havoc(&x);
  return x;
}
int main()
{
	int a[N];
	int b[N];
	int k;
	int i;

  //@ transform flatten I;
	for (i  = 0; i<N ; i++)
	{
		a[i] = i; 
		b[i] = i ;
	}

  //@ transform flatten I;
	for (i=0; i< N; i++)
	{
		if(__VERIFIER_nondet_short())
		{
			k = __VERIFIER_nondet_short();
			a[i] = k;
			b[i] = k * k ;
		}
	}

  // Modified
  //@ transform flatten I;
  $assert($forall(int i:0..N-1) a[i] == b[i] || b[i] == a[i] * a[i]);
  // Original
  /*
	for (i=0; i< N; i++)
	{
		__VERIFIER_assert(a[i] == b[i] || b[i]  == a[i] * a[i]);
	}
  */
}

