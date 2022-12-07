/* Cannot verify. Reasons:
 * CIVL does not have support for specifying heap behavior so cannot express facts about what occurs in init.
 * Barring the first reason, our technique would not reduce the number of loop invariants needed because there are no loop independent writes.
 */
typedef unsigned int size_t;
#include <stdlib.h>
#include <assert.h>
#pragma CIVL ACSL
#define NULL 0
$input int N;
$assume(0 < N);

typedef struct
{
	int *n;
}S;

extern int __VERIFIER_nondet_int(void) {
  int x;
  $havoc(&x);
  return x;
}

int * abstract_malloc() {
  if (__VERIFIER_nondet_int() == 0) {
    return (int *) malloc(sizeof(int));
  } else {
    return NULL;
  }
}

void init(S a[], int size)
{
	int i;
	for(i = 0; i < size; i++)
	{
		a[i].n = abstract_malloc();
	}
}

int main()
{
	S a[N];
	int i;
	int flag;
	flag = 0;

	init(a, N);

	for(i = 0; i < N; i++)
	{
		if(a[i].n != NULL)
		{
			flag = 1;
		}
	}


  // Modified
  $assert($forall(int i:0..N-1) flag == 0 => a[i].n == NULL);
  // Original
  /*
	for(i = 0; i < N; i++)
	{
		if (flag == 0)
		__VERIFIER_assert(a[i].n == NULL);
	}
  */
	
	return 0;
}	
