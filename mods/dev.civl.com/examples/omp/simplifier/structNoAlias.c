#include "omp.h"
#define N 10

typedef struct {
  int a[N];
  int b[N];
} T;

int main()  {
  T s;
  T *p, *q;
  
  p = &s; q = p;  
  for (int i = 0; i < N; i++)   
    p->b[i] = 0;
#pragma omp parallel for firstprivate(p, q) shared(s)
  for (int i = 0; i < N; i++) {
    p->a[i] += q->b[i];    
  }
}
