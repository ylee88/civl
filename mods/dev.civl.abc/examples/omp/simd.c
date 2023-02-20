#include <omp.h>


int main () 
{
  int a[32];
#pragma omp simd  lastprivate(a) simdlen(8) safelen(16)
  for (int i = 0; i < 32; i++)
    a[i] = i;

  return a[0];
}
