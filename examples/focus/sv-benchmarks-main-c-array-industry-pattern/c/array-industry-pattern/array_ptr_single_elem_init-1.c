/* Cannot verify. Reasons:
 * Correctness of algorithm depends on using array of pointers which are later dereferenced. This causes a CIVL bug to appear.
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
$assume(3 < N);

struct S
{
	int *p;
	int n;
};

struct S *a[ N];

int main()
{

	int i;

	//init and update
  //@ transform flatten i;
	for (i = 0; i <  N; i++)
	{
		int q = __VERIFIER_nondet_int();
		struct S *s = NULL;
		if (q == 0)
		{
			s = (struct S*) malloc(sizeof(struct S));
			s->n = q % 2;
		}
		if (s != 0)
		{
			if (s->n == 0)
			{
				s->p = (int *) malloc(sizeof(int));
			}
			else
			{
				s->p = NULL;
			}
		}

		a[i] = s;
	}

	a[3] = (struct S*) malloc(sizeof(struct S));

  // modified:
  //@ transform flatten i;
  $assert($forall (int i:0..N-1) ((i != 3 && a[i] != NULL && a[i].n == 0) => a[i].p != NULL));
  /* original:
	//check 2
	for (i = 0; i <  N; i++)
	{
		struct S *s1 = a[i];
		if (i != 3 && s1 != NULL && s1->n == 0)
		{
			__VERIFIER_assert(s1->p != NULL);
		}
	}
  */
	return 0;
}

