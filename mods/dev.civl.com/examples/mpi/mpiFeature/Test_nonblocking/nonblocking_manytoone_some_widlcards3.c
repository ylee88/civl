#include "mpi.h"
#include "assert.h"

int many_to_one () {
  int size;
  int rank;
  MPI_Comm comm = MPI_COMM_WORLD;
  MPI_Datatype type = MPI_INT;
  
  MPI_Comm_size (comm, &size);
  MPI_Comm_rank (comm, &rank);
  if (size < 3)
    return 0;

  int dat[3] = {rank * size + 1, rank * size + 2, rank * size + 3};
    
  MPI_Send(dat, 1, MPI_INT, 0, 1, comm);
  MPI_Send(dat + 1, 1, MPI_INT, 0, 2, comm);
  MPI_Send(dat + 2, 1, MPI_INT, 0, 3, comm);
  if (rank == 0) {
    int ub = 3 * size;
    int rbuf[ub];
    MPI_Request reqs[ub];
    MPI_Status statuses[ub];
    int oft = 0;
    
    MPI_Irecv(rbuf + oft, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, comm, reqs + oft);
    oft++;
    MPI_Irecv(rbuf + oft, 1, MPI_INT, 0, MPI_ANY_TAG, comm, reqs + oft);
    oft++;
    MPI_Irecv(rbuf + oft, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, comm, reqs + oft);
    oft++;
    MPI_Irecv(rbuf + oft, 1, MPI_INT, 1, MPI_ANY_TAG, comm, reqs + oft);
    oft++;
    MPI_Irecv(rbuf + oft, 1, MPI_INT, 2, MPI_ANY_TAG, comm, reqs + oft);
    oft++;
    for (int i = 0; i < ub - 5; i++) {
      MPI_Irecv(rbuf + oft, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, comm, reqs + oft);
      oft++;
    }
    MPI_Waitall(ub, reqs, statuses);
    for (int i = 0; i < ub; i++) {
      int src = statuses[i].MPI_SOURCE;
      int tag = statuses[i].MPI_TAG;
      
      assert (rbuf[i] == src * size + tag);
    }
  }
  return 0;
}

int main() {
  MPI_Init(NULL, NULL);
  many_to_one();
  MPI_Finalize();
  return 0;
}
