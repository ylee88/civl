/* Verifiable with mods:
 * Changed S *a[N] to S a[N] because of CIVL bug: Pointer symbolic info not simplified before dereference. Trying to fix this leads to deeper CIVL bug.
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
struct _S
{
	int *p;
	int n;
};
typedef struct _S S;

// modified:
S a[N];
// original:
//S *a[N];
int user_read()
{
	int x = __VERIFIER_nondet_int();
	return x;
}

int main()
{
	int i;

  // modified:
  //@ transform flatten I;
	for(i = 0; i < N; i++)
	{
		S s1;
		
		s1.n = user_read();
		
		if(s1.n == 1)
		{
			s1.p = (int *)malloc(sizeof(int));
		}
		else
		{
			s1.p = (void *)0;
		}
		a[i] = s1;
	}
  /* original:
  for(i = 0; i < N; i++)
	{
		S *s1 =  (S *)malloc(sizeof(S));
		
		s1 -> n = user_read();
		
		if(s1 ->n == 1)
		{
			s1 -> p = (int *)malloc(sizeof(int));
		}
		else
		{
			s1 -> p = (void *)0;
		}
		a[i] = s1;
	}
  */

  // Modified
  //@ transform flatten I;
  $assert($forall(int i:0..N-1) (a[i].n == 1) == (a[i].p != (void *)0));
  // Original
  /*
	for(i = 0; i < N; i++)
	{
		S *s2 = a[i];

		if(s2 ->n == 1)
		{
			__VERIFIER_assert(s2 -> p != (void *)0);
		}
	}
  */

  /*
  //@ transform flatten I;
  for(i = 0; i < N; i++)
  {
    if (a[i]->n == 1) {
      free(a[i]->p);
    }
    free(a[i]);
  }
  */
	return 0;
}
