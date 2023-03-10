/* Cannot verify
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
$assume (15000 < N);
/* 
   Implements a set. Inserts elements from an array of values into the set. Then removes an element from the set and then checks that the removed item is not present in the set.
*/


int insert( int set [] , int size , int value ) {
  set[ size ] = value;
  return size + 1;
}

int elem_exists( int set [ ] , int size , int value ) {
  // modified:
  return $exists(int i:0..size-1) set[i] == value;
  /* original:
  int i;
  for ( i = 0 ; i < size ; i++ ) {
    if ( set[ i ] == value ) return 1;
  }
  return 0;
  */
}

int main( ) {
  int i, pos, n = 0, found = 0;
  int set[ N ];       // set for storing values
  int values[ N ];    // array of values to be inserted in the array
  int element;           // element to be removed

 	for ( i = 0 ; i < N ; i++ ) {
		set[i] = __VERIFIER_nondet_int();
		values[i] = __VERIFIER_nondet_int();
	}
	
	element = __VERIFIER_nondet_int(); 

  int v;
  for ( v = 0 ; v < N ; v++ ) {
    if ( elem_exists( set , n , values[ v ] ) != 1 ) {
      // parametes are passed by reference
      n = insert( set , n , values[ v ] );
    }
  }
  

    for (i = 0; i < N && found != 1; i++) {
        if (set[i] == element)
        {
            found = 1;
            pos = i;
        }
    }
    if ( found == 1 )
    {
      //@ transform flatten i;
        for (i = pos; i <  N - 1; i++)
        {
            set[i] = set[i + 1];
        }
    }

    if(found == 1) 
    {
      // modified:
      //@ transform flatten i;
      $assert($forall(int i:0..N-2) set[i] != element);
      /* original:
      for(i=0; i < N - 1; i++)
      {
        __VERIFIER_assert(set[i] != element);
      }
      */
    }
}
