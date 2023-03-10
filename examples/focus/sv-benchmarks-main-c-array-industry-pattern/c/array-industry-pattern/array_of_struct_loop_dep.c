/* Cannot verify. Reasons:
 * Assertion inside of dependent loop
 */
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL

$input int N;
$assume(0 < N);
struct _S
{
	int n;
};
typedef struct _S S;

S a[N];

int main()
{
	int i;
	for(i = 0; i < N; i++)
	{
		a[i].n = 10;
	}


	for(i = 0; i < N; i++)
	{
		__VERIFIER_assert(a[i].n == 10 || a[i].n == 20);

		if(i+1 != 15000)
			a[i+1].n = 20;
		
	}

	return 0;
}

