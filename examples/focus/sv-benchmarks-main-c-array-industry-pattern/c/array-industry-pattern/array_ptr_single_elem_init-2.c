/* Bug found in assertion!
 * Fixed and verified (although missing uninitialized array element read bug)
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
$assume(15000 < N);

int main()
{

	int i;
	int a[N];
	int b[N];
	int c[N];

	//init and update
  //@ transform flatten i;
	for (i = 0; i < N; i++)
	{
		int q = __VERIFIER_nondet_int();
		a[i] = 0;
		if (q == 0)
		{
			a[i] = 1;
			b[i] = i % 2;
		}
		if (a[i] != 0)
		{
			if (b[i] == 0)
			{
				c[i] = 0;
			}
			else
			{
				c[i] = 1;
			}
		}
	}

	a[15000] = 1;

  // fixed (mimicing structure of array_ptr_single_elem_init-1.c)
  //@ transform flatten i;
  $assert($forall(int j:0..N-1) ((j != 15000 && a[j] != 0 && b[j] == 0) => c[j] == 0));
  
  //$assert($forall (int i:0..N-1) ((i != 3 && a[i] != NULL && a[i].n == 0) => a[i].p != NULL));
  /* modified (with bug):
  // transform flatten i;
  $assert($forall(int i:0..N-1) i == 15000 => a[i] != 1);
  */
  /* original:
	for (i = 0; i < N; i++)
	{
		if(i==15000)
		{
			__VERIFIER_assert(a[i] != 1); 
		}
	}
  */
	return 0;
}

