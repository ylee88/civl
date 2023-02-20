#include <stdio.h>
#include <assert.h>
#include "sumlib.h"
#define N 100
int main() {
  int result = sum(N);
  printf("N=%d, sum = %d\n", N, result);
  assert(result == (N+1)*N/2);
}
