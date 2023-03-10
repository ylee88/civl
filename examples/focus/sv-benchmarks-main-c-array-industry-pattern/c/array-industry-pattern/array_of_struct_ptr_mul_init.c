/* Verifiable!
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
$input int N;
$assume(0 < N);
struct S {
	unsigned short p;		  
	unsigned short q;		 		  
} a[N];

short __VERIFIER_nondet_short() {
  short x;
  $havoc(&x);
  return x;
}
unsigned char __VERIFIER_nondet_uchar() {
  unsigned char x;
  $havoc(&x);
  return x;
}
int main()
{
	unsigned char k;
	
	int i;
  //@ transform flatten I;
	for (i  = 0; i < N ; i++)
	{
		a[i].p = i; 
		a[i].q = i ;
	}

  //@ transform flatten I;
	for (i = 0; i < N; i++)
	{
		if ( __VERIFIER_nondet_short())
		{
			k = __VERIFIER_nondet_uchar();
			a[i].p = k;
			a[i].q = k * k ;
		}
	}

  // Modified
  //@ transform flatten I;
  $assert($forall(int i:0..N-1)a[i].p == a[i].q || a[i].q == a[i].p * a[i].p);
  // Original
  /*
	for (i = 0; i < N; i++)
	{
		__VERIFIER_assert(a[i].p == a[i].q || a[i].q == a[i].p * a[i].p);
	}
  */

	return 0;
}

