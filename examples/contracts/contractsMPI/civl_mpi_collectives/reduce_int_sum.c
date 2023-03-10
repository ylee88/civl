#include<mpi.h>
#include<civl-mpi.cvh>
#include<pointer.cvh>
#include<civlc.cvh>
#include<string.h>
#include<stdlib.h>

/* A collective sum-reduction operation */

#pragma CIVL ACSL

/*@ \mpi_collective(comm, P2P):
  @   requires 0 <= count && 0 <= count*\mpi_extent(datatype);
  @   requires \mpi_valid(sendbuf, count, datatype);
  @   requires \mpi_valid(recvbuf, count, datatype);
  @   requires \mpi_agree(root) && \mpi_agree(count * datatype);
  @   requires 0 <= root && root < \mpi_comm_size;
  @   behavior root:
  @     assumes \mpi_comm_rank == root;
  @     assigns \mpi_region(recvbuf, count, datatype);
  @     ensures  sendbuf != MPI_IN_PLACE ==> 
  @                ( \forall integer i; 0<= i && i <count ==> 
  @                     recvbuf[i] == \sum(0, \mpi_comm_size-1, 
  @                        \lambda int k; \on(k, sendbuf[i]))
  @                );
  @
  @*/
int reduce_sum(void * sendbuf, void * recvbuf, MPI_Datatype datatype,
	       int count, int root, MPI_Comm comm) {
  int rank;
  int tag;
  
  MPI_Comm_rank(comm, &rank);
  if (rank != root)
    MPI_Send(sendbuf, count, datatype, root, tag, comm);
  else {
    int nprocs;

    MPI_Comm_size(comm, &nprocs);
    memcpy(sum, sendbuf, size);
    for (int i = 0; i<nprocs; i++) {
      if (i != root){
	MPI_Recv(recvbuf, count, datatype, i, REDUCE_TAG, comm, MPI_STATUS_IGNORE);

	for (int i = 0; i < count; i++) 
	  sum[i] = sum[i] + recvbuf[i];
      }
    }
    memcpy(recvbuf, sum, size);
    free(sum);
  }
  return 0;
}
