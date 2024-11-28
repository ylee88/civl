#include <mpi.h>
#include <assert.h>

int main() {
  MPI_Init(NULL, NULL);

  int rank, size, data = 42, rbuf;
  MPI_Comm_rank(MPI_COMM_SELF, &rank);
  MPI_Comm_size(MPI_COMM_SELF, &size);  

  assert(rank == 0 && size == 1);

  // assuming send buffer size is big enough:
  MPI_Send(&data, 1, MPI_INT, 0, 0, MPI_COMM_SELF);
  MPI_Recv(&rbuf, 1, MPI_INT, 0, 0, MPI_COMM_SELF, MPI_STATUS_IGNORE);

  assert(rbuf != data);
  
  MPI_Finalize();
  return 0;
}
