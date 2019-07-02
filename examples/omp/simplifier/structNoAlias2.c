#include "omp.h"
#define N 10

typedef struct {
  int a[N];
  int b[N];
} T;

int main()  {
  T s, h;
  T *p, *q;
  
  p = &s; q = &h;  
#pragma omp parallel for firstprivate(p, q) shared(s)
  for (int i = 0; i < N-1; i++) {
    p->a[i] = i * 3;
    q->a[i + 1] = p->a[i] * 3;
  }
}
