#include "omp.h"
#define N 10


int main()  {
  int a[N*10][N];
  int (*p)[N], (*q)[N];
  
  p = a; q = p + 3;  
#pragma omp parallel for firstprivate(p, q) shared(a)
  for (int i = 0; i < N-1; i++) 
    for (int j = 0; j < N-1; j++) {
      q[i][j] = p[i][j] + 3;
    }
}
