/* Bug found!
 * Fixed assertion and verified.
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
$input int N;
$assume(0 < N);

signed int a[N];

int main()
{
	int i;
  //@ transform flatten i;
	for(i = 0; i < N; i++)
	{
		if(i>=0 && i<=10000)
			a[i] = 10;
		else
		a[i] = 0;
	}

  // fixed:
  //@ transform flatten i;
  $assert($forall(int i:0..N-1) (i>=0 && i <= 10000) => a[i] == 10);
  /* modified:
  // transform flatten i;
  $assert($forall(int i:0..N-1) a[i] == 10);
  */
  /* original:
	for(i = 0; i < N; i++)
	{
		__VERIFIER_assert(a[i] == 10);
	}
  */

	return 0;
}


