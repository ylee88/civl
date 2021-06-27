#include <mpi.h>
#include <stdio.h>

#define buff_size 128

int main(int argc, char **argv) {
  int nprocs = -1;
  int rank = -1;
  int root = 0;
  int size = 1, j = 0;

  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  printf("Hello from rank %d \n", rank);

  if (nprocs < 2)
    printf("This test needs at least 2 processes to produce a bug!\n");

  MPI_Comm newcom = MPI_COMM_WORLD;
  MPI_Op op = MPI_SUM;
  MPI_Datatype type = MPI_INT;

  int dbs = sizeof(int) * nprocs;

  op = MPI_OP_NULL;

  int sum1, val1 = 1;

  MPI_Reduce(&val1, &sum1, 1, type, op, root, newcom);

  MPI_Finalize();
  printf("Rank %d finished normally\n", rank);
  return 0;
}
