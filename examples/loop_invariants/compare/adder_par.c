#include<stdio.h>
#include<stdlib.h>
#include<mpi.h>
#include<civlc.cvh>
#pragma CIVL ACSL
$input int N;
$assume(N > 0);
$input double a[N];
$output double sum_out;
double sum_global, sum_local = 0.0;
int pid, nprocs;
int main() {
  MPI_Init(NULL, NULL);
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &pid);
  int first = N*pid/nprocs, afterLast = N*(pid+1)/nprocs;
  /*@ loop invariant first <= i <= afterLast;
    @ loop invariant sum_local == \sum(first, i-1, \lambda int t; a[t]);
    @*/
  for (int i=first; i<afterLast; i++) 
    sum_local += a[i];
  MPI_Reduce(&sum_local, &sum_global, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
  if (pid == 0) {
    printf("impl: sum=%lf\n", sum_global);
    sum_out = sum_global;
  }
  MPI_Finalize();
}
