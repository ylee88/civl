#include "mpi.h"
#include "assert.h"

int main(int argc, char *argv[])  {
  int numtasks, rank, next, prev, buf[2], tag1=1, tag2=2;
  MPI_Request reqs[4];   
  MPI_Status stats[4];   

  MPI_Init(&argc,&argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);

  MPI_Request req1, req2;
  int rbuf[2];
  MPI_Comm comm = MPI_COMM_WORLD;
  
  if (rank == 0) {    
    MPI_Irecv(rbuf, 1, MPI_INT, 1, 0, comm, &req1);
    MPI_Irecv(rbuf + 1, 1, MPI_INT, 1, 0, comm, &req2);
    MPI_Wait(&req2, MPI_STATUS_IGNORE);
    rbuf[0]++;
    MPI_Send(rbuf, 1, MPI_INT, 1, 0, comm);
    MPI_Wait(&req1, MPI_STATUS_IGNORE);
    assert (rbuf[1] == 3);
    rbuf[1]++;
    MPI_Send(rbuf + 1, 1, MPI_INT, 1, 0, comm);
  } else if (rank == 1) {
    MPI_Irecv(rbuf, 1, MPI_INT, 0, 0, comm, &req1);
    MPI_Irecv(rbuf + 1, 1, MPI_INT, 0, 0, comm, &req2);
    MPI_Send(&rank, 1, MPI_INT, 0, 0, comm);
    MPI_Wait(&req1, MPI_STATUS_IGNORE);
    rbuf[0]++;
    MPI_Send(rbuf, 1, MPI_INT, 0, 0, comm);
    MPI_Wait(&req2, MPI_STATUS_IGNORE);
    assert (rbuf[1] == 4);    
  }
  MPI_Finalize();
}
