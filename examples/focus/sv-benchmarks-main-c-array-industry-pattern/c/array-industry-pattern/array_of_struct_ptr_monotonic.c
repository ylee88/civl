/* Verifiable with mods:
 * Loop step of 2 changed to 1
 * S *a[N] changed to S a[N] due to CIVL bug.
 */
typedef unsigned int size_t;
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
extern int __VERIFIER_nondet_int(void) {
  int x;
  $havoc(&x);
  return x;
}

$input int N;
$assume(0 < N);

struct S
{
	int t;
	int * p1;
};

// modifed:
struct S a[N];
// original:
//struct S* a[N];

int main()
{
	int i;
  // modified:
  //@ transform flatten i;
	for(i = 0; i < N; i++)
	{
		struct S s;
		s.t = __VERIFIER_nondet_int();
		if (s.t == 10)
			s.p1 = (int *) malloc(sizeof(int));
		a[i] = s;
	}
  /* original:
  for(i = 0; i < N; i = i+2)
	{
		struct S* s = (struct S*) malloc(sizeof(struct S));
		s->t = __VERIFIER_nondet_int();
		if (s->t == 10)
			s->p1 = (int *) malloc(sizeof(int));
		a[i] = s;
	}
  */

  // modified:
  //@ transform flatten i;
  $assert($forall(int i:0..N-1) a[i].t == 10 => a[i].p1 != (void *) 0);
  /* original:
	for(i = 0; i < N; i = i+2)
	{
		struct S* u = a[i];
		if (u->t == 10)
		{
			__VERIFIER_assert(u->p1 != (void *) 0);
		}
	}
  */
}
