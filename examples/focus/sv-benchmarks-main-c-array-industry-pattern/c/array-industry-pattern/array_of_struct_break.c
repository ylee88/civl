/* Cannot verify. Reasons:
 * Contains break statement.
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
$assume(0 < N);
struct S
{
	int n;
};

struct S s[N];

int main()
{
	int i;
	int c;
	c = __VERIFIER_nondet_int();
	for(i = 0; i < N; i++)
	{
		if(c > 5)
			break;

		s[i].n = 10;
	}

  // Modified
  $assert($forall(int i:0..N-1) c <= 5 ==> s[i].n == 10);
  // Original
  /*
	for(i = 0; i < N; i++)
	{
		if(c <= 5)
			__VERIFIER_assert(s[i].n == 10);
	}
  */

	return 0;
}
