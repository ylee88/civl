/* Verifiable!
 * Note: must increase lower bound of N to be at least 3, otherwise program is incorrect.
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
extern int __VERIFIER_nondet_int(void) {
  int x;
  $havoc(&x);
  return x;
}
$input int N;
$assume(3 < N);

struct S
{
	int p;
	int n;
};

struct S a[N];

int main()
{

	int i;

  //@ transform flatten I;
	//init and update
	for (i = 0; i < N; i++)
	{
		int q = __VERIFIER_nondet_int();
		struct S s;
		
	  s.n = __VERIFIER_nondet_int();
			
		if (s.n == 0)
		{
		    s.p =10 ;
		}
		else
		{
	 		s.p = 20;
		}
		
		a[i] = s;
	}

	a[3].p = 30;
	a[3].n = 40;

  // Modified
  //@ transform flatten I;
  $assert($forall(int i:0..N-1) (i != 3 && a[i].n == 0) => a[i].p == 10);
  // Original
  /*
	//check 2
	for (i = 0; i < N; i++)
	{
		struct S s1 = a[i];
		if (i != 3 && s1.n == 0)
		{
			__VERIFIER_assert(s1.p == 10); 
		}
	}
  */
	return 0;
}

