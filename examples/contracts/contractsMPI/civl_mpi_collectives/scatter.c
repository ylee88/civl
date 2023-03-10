#include<mpi.h>
#include<civl-mpi.cvh>
#include<civlc.cvh>
#include<string.h>

#pragma CIVL ACSL

/*@ \mpi_collective(comm, COL):
  @   requires \mpi_agree(root) && \mpi_agree(recvcount * \mpi_extent(recvtype));
  @   requires 0 <= root && root < \mpi_comm_size;
  @   requires sendcount >= 0 && sendcount * \mpi_extent(sendtype) >= 0;
  @   requires recvcount >= 0 && recvcount * \mpi_extent(recvtype) >= 0;
  @   waitsfor root;
  @   behavior imroot_not_inplace:
  @     assumes \mpi_comm_rank == root && recvbuf != MPI_IN_PLACE;
  @     requires \mpi_valid(recvbuf, recvcount, recvtype);
  @     requires \mpi_extent(sendtype) * sendcount == 
  @              \mpi_extent(recvtype) * recvcount;
  @     requires \mpi_valid(sendbuf, sendcount * \mpi_comm_size, sendtype);
  @     assigns  \mpi_region(recvbuf, recvcount, recvtype);
  @     ensures   \mpi_equals(
  @                           \mpi_region(recvbuf, recvcount, recvtype), 
  @                           \mpi_region(\mpi_offset(sendbuf, \mpi_comm_rank * sendcount, sendtype), 
  @                                       sendcount, sendtype));
  @   behavior imroot_inplace:
  @     assumes  \mpi_comm_rank == root && recvbuf == MPI_IN_PLACE;
  @     requires \mpi_extent(sendtype) * sendcount == 
  @              \mpi_extent(recvtype) * recvcount;
  @     requires \mpi_valid(sendbuf, sendcount * \mpi_comm_size, sendtype);
  @     assigns  \nothing;
  @   behavior noroot:
  @     assumes \mpi_comm_rank != root;
  @     requires recvbuf != MPI_IN_PLACE;
  @     requires \mpi_valid(recvbuf, recvcount, recvtype);
  @     assigns  \mpi_region(recvbuf, recvcount, recvtype);
  @     ensures \mpi_equals(\mpi_region(recvbuf, recvcount, recvtype),
  @                         \mpi_region(\mpi_offset(\on(root, sendbuf), recvcount * \mpi_comm_rank, recvtype),
  @                                     recvcount, recvtype));
 */
int scatter(const void* sendbuf, int sendcount, MPI_Datatype sendtype, 
	    void* recvbuf, int recvcount, MPI_Datatype recvtype, int root,
	    MPI_Comm comm){
  int rank, nprocs;
  int tag = 999;
  
  MPI_Comm_rank(comm, &rank);
  MPI_Comm_size(comm, &nprocs);
  /* MPI_standard requirement: 
   * Only root process can use MPI_IN_PLACE */
  if (rank == root && recvbuf != MPI_IN_PLACE) {
    void * ptr;
    
    ptr = $mpi_pointer_add(sendbuf, root*sendcount, sendtype);
    memcpy(recvbuf, ptr, sizeofDatatype(recvtype)*recvcount);
  }
  /* Root process scatters data to other processes */
  if(rank == root)
    for(int i=0; i<nprocs; i++){
      if(i != root) {
	void * ptr;
	
	ptr = $mpi_pointer_add(sendbuf, i * sendcount, sendtype);
	$mpi_collective_send(ptr, sendcount, sendtype, i, tag, comm);
      }
    }
  /* Non-root processes receive data */
  if(!(root == rank)){
    int real_recvcount;
    MPI_Status status;
    
    $mpi_collective_recv(recvbuf, recvcount, recvtype, 
			 root, tag, comm, &status, "MPI_Scatter");
    real_recvcount = status.size/sizeofDatatype(recvtype);
  }
  return 0;
}
