#include <stdio.h>
#include <assert.h>
#define N 6

void func(int n, double A[restrict]) {
  double s = 0.0;
#pragma omp simd simdlen(2)
  for (int i = 0; i < 2*n; ++i) {
#pragma civl depend source(A)
  S1:
    s += A[i];
#pragma civl depend target(A)
  S2:
    A[i] = 10.0*i;
  }
  printf("s=%lf\n", s);
  assert(s == 2*N*(2*N-1)/2);
}

int main(void) {
  double A[2*N];
  for (int i=0; i<2*N; i++)
    A[i] = i;
  func(N, A);
  for (int i=0; i<2*N; i++)
    assert(A[i] == 10.0*i);
}