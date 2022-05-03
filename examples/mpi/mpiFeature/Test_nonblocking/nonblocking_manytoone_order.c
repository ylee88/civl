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
    int dat[4] = {rank, rank + 1, rank + 2, rank + 3};
    MPI_Request reqs[4];
    
    for (int i = 0; i < 4; i++)
      MPI_Isend(dat + i, 1, MPI_INT, 0, i, comm, reqs + i);
    MPI_Waitall(4, reqs, MPI_STATUS_IGNORE);    
  } else {
    int ub = 4 * (size - 1);    
    int dat[ub];
    MPI_Request reqs[ub];
    MPI_Status statuses[ub];
    
    for (int i = 1; i < size; i++)
      for (int j = 0; j < 4; j++)
	MPI_Irecv(dat + (i-1) * 4 + j, 1, MPI_INT, i, j, comm, reqs + (i-1) * 4 + j);
    MPI_Waitall(ub, reqs, statuses);
    for (int i = 1; i < size; i++)
      for (int j = 0; j < 4; j++) {
	int idx = (i-1)*4 + j;
	int src = statuses[idx].MPI_SOURCE;
	int tag = statuses[idx].MPI_TAG;
	
	assert (dat[idx] == i + j && src == i && j == tag);
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
