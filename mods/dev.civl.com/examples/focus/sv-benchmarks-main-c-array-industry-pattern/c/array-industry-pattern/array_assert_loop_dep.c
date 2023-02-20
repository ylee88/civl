/* Cannot verify. Reasons:
 * assertion inside of dependent for loop
 */
#include <stdlib.h>
#include <assert.h>

#pragma CIVL ACSL

$input int N;
$assume(0 < N);

int main()
{
	int i;
	int a[N];
	for(i = 0; i < N; i++)
	{
		a[i] = 10;
	}


	for(i = 0; i < N; i++)
	{
		$assert(a[i] == 10);

		if(i+1 != 15000)
			a[i+1] = 20;
	}

	return 0;
}

