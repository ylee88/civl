/* An reproduce of an advanced reduce-scatter algorithm presented in
 * paper "Efficient Implementation of Reduce-Scatter in MPI"
 * (https://dl.acm.org/citation.cfm?id=1895529) with simple stubs for
 * functions that are omitted in the paper.
 */

#include "mpi.h"
#include "stdlib.h"
#include "stdio.h"
#include "string.h"
#include "assert.h"

#define LOW  0
#define HIGH 1
#define MAX(a, b) if(*(a) < *(b)) *(a) = *(b)
#define MIN(a, b) if(*(a) > *(b)) *(a) = *(b)

void init_offsets(int * offsets, int size, int *recvcnts);

void init_s_high(double * s_high, int n);

void reduce(double * buf0, double * buf1, int n, MPI_Op op);

int reduce_scatter_double(void *sendbuf, void *recvbuf, int *recvcnts,
			  MPI_Op op, MPI_Comm comm) 
{
  int rank, size;

  MPI_Comm_rank(comm, &rank);
  MPI_Comm_size(comm, &size);

  int offs[size];
  double * tarrsend, *tarrrecv;
  double * s[2];
  
  init_offsets(offs, size, recvcnts);
  tarrsend = (double*)sendbuf + offs[rank];
  tarrrecv = (double *)malloc(sizeof(double) * recvcnts[rank]);
  s[LOW] = (double *)malloc(sizeof(double) * recvcnts[rank]);
  s[HIGH] = (double *)malloc(sizeof(double) * recvcnts[rank]);
  init_s_high(s[HIGH], recvcnts[rank]);
  memcpy(s[LOW], tarrsend, sizeof(double) * recvcnts[rank]);
  
  for (int h = 1; h < size; h++) {
    int dst = (rank + h) % size;
    int src = (rank + size - h) % size;
    
    tarrsend = (double*)sendbuf + offs[dst];
    MPI_Sendrecv(tarrsend, recvcnts[dst], MPI_DOUBLE, dst, h, tarrrecv,
		 recvcnts[rank], MPI_DOUBLE, src, h, comm, MPI_STATUS_IGNORE);
    if (src < rank)
      reduce(s[LOW], tarrrecv, recvcnts[rank], op);
    else 
      reduce(s[HIGH], tarrrecv, recvcnts[rank], op);
  }
  reduce(s[LOW], s[HIGH], recvcnts[rank], op);
  memcpy(recvbuf, s[LOW], recvcnts[rank] * sizeof(double));
  // finish up:
  free(tarrrecv);
  free(s[LOW]);
  free(s[HIGH]);
  return 0;
}

void init_offsets(int * offsets, int size, int *recvcnts) {
  offsets[0] = 0;
  for (int i = 1; i < size; i++)
    offsets[i] = offsets[i - 1] + recvcnts[i - 1];
}

void init_s_high(double * s_high, int n) {
  for (int i = 0; i < n; i++) s_high[i] = 0;
}

void reduce(double * buf0, double * buf1, int n, MPI_Op op) {
  switch (op) {
  case MPI_MAX:
    for (int i = 0; i < n; i++)
      MAX(buf0+i, buf1+i);
    break;
  case MPI_MIN:
    for (int i = 0; i < n; i++)
      MIN(buf0+i, buf1+i);    
    break;
  case MPI_SUM:
    for (int i = 0; i < n; i++)
      buf0[i] += buf1[i];
    break;
  default:
    assert(0);
  }
}

