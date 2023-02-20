#include <omp.h>
#include <stdio.h>
int N=2;
int main() {
  int a[N], b[N];
#pragma omp parallel
  {
    int i, tid=omp_get_thread_num();
#pragma omp for nowait
    for (i=0; i<N; i++) {
      $atomic{ printf("Thread %d writes a[%d]\n", tid, i); a[i]=i; }
    }
#pragma omp for
    for (i=0; i<N; i++) {
      $atomic{ printf("Thread %d reads a[%d]\n", tid, i); b[i]=2*a[i]; }
    }
  }
}
