#include "mpi.h"
#include "assert.h"

#ifndef TAG
#define TAG 0
#endif

#ifndef WILDCARD
#define SRC 0
#else
#define SRC MPI_ANY_SOURCE
#endif

int main(int argc, char *argv[])  {
  int numtasks, rank;
  
  MPI_Init(&argc,&argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  
  if (numtasks < 2) {
    MPI_Finalize();
    return 0;
  }

  MPI_Comm comm = MPI_COMM_WORLD;
  MPI_Request reqs[4];
  
  if (rank == 0) {
    int dat[4] = {1,2,3,4};

    MPI_Isend(dat, 1, MPI_INT, 1, TAG, comm, reqs);
    MPI_Isend(dat + 1, 1, MPI_INT, 1, TAG, comm, reqs + 1);
    MPI_Isend(dat + 2, 1, MPI_INT, 1, TAG, comm, reqs + 2);
    MPI_Isend(dat + 3, 1, MPI_INT, 1, TAG, comm, reqs + 3);
    MPI_Waitall(4, reqs, MPI_STATUS_IGNORE);
  } else if (rank == 1) {
    int rbuf[4];
    
    MPI_Irecv(rbuf, 1, MPI_INT, SRC, TAG, comm, reqs);
    MPI_Irecv(rbuf + 1, 1, MPI_INT, SRC, TAG, comm, reqs + 1);
    MPI_Irecv(rbuf + 2, 1, MPI_INT, SRC, TAG, comm, reqs + 2);
    MPI_Irecv(rbuf + 3, 1, MPI_INT, SRC, TAG, comm, reqs + 3);
    MPI_Waitall(4, reqs, MPI_STATUS_IGNORE);
    assert (rbuf[0] == 1 && rbuf[1] == 2 && rbuf[2] == 3 && rbuf[3] == 4);
  }
  MPI_Finalize();
}
