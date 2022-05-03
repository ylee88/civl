#include "mpi.h"
#include "assert.h"

int many_to_one () {
  int size;
  int rank;
  MPI_Comm comm = MPI_COMM_WORLD;
  MPI_Datatype type = MPI_INT;
  
  MPI_Comm_size (comm, &size);
  MPI_Comm_rank (comm, &rank);

  if (rank != 0) {
    int dat[3] = {rank, rank + 1, rank + 2};
    MPI_Request reqs[3];
    
    for (int i = 0; i < 3; i++)
      MPI_Isend(dat + i, 1, MPI_INT, 0, i, comm, reqs + i);
    MPI_Waitall(3, reqs, MPI_STATUS_IGNORE);    
  } else {
    int ub = 3 * (size - 1);    
    int dat[ub];
    MPI_Request reqs[ub];
    MPI_Status statuses[ub];
    
    for (int i = 0; i < ub; i++) 
      MPI_Irecv(dat + i, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, comm, reqs + i);
    MPI_Waitall(ub, reqs, statuses);
    for (int i = 0; i < ub; i++) {
      int src = statuses[i].MPI_SOURCE;
	int tag = statuses[i].MPI_TAG;
	
	assert (dat[i] == src + tag);
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
