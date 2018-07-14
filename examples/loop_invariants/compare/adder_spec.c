#include<stdio.h>
#include<stdlib.h>
#include <civlc.cvh>
#pragma CIVL ACSL
$input int N;
$assume(N >= 0);
$input int a[N];
$output double sum_out;
int main() {
  double sum = 0;
  /*@ loop invariant 0 <= i <= N;
    @ loop invariant sum == \sum(0, i-1, \lambda int t; a[t]);
    @*/
  for (int i=0; i<N; i++) sum += a[i];
  printf("spec: sum=%lf\n", sum);
  sum_out = sum;
}
