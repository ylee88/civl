/* Verifiable!
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
$input int N;
$assume(0 < N);

int *a[N];
int i;
int main ()
{
  //@ transform flatten half;
	for(i = 0; i < N; i++)
	{
		a[i] = NULL;
	}

  //@ transform flatten half;
	for(i = 0; i < N / 2; i++)
	{
		a[i] = malloc(sizeof(int)) ;
	}

  // modified:
  //@ transform flatten half;
  $assert($forall(int i:0..(N/2)-1) a[i] != NULL);
  /* original:
	for(i = 0; i < N / 2; i++)
	{
		__VERIFIER_assert(a[i] != NULL);
	}
  */

return 0;
}	


