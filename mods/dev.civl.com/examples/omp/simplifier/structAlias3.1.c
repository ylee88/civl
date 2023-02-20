#include "omp.h"
#define N 10

typedef  struct {
  int b[N][N];
} H;

typedef struct {
  int a[N][N];
  H * h;
} T;

int main()  {
  T s, s1;
  H h;
  T *p, *q;

  s.h = &h; s1.h = &h;
  p = &s; q = &s1;  
#pragma omp parallel for firstprivate(p, q) shared(s)
  for (int i = 0; i < N-1; i++) 
    for (int j = 0; j < N; j++) {
      p->h->b[i][j] = q->h->b[i + 1][j];
    }
}
