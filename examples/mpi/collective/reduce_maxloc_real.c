#include <assert.h>
#include <mpi.h>

struct Pair {
  double val;
  int rank;
};

int main(void) {
  MPI_Init(NULL, NULL);
  int rank, nprocs;
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  double mynum = rank < nprocs/2.0 ? rank : nprocs - rank - 1;
  struct Pair mypair = { mynum, rank }, result;
  MPI_Reduce(&mypair, &result, 1, MPI_DOUBLE_INT, MPI_MAXLOC,
	     0, MPI_COMM_WORLD);
  if (rank == 0) {
    assert(result.val == (nprocs-1)/2);
    assert(result.rank == (nprocs-1)/2);
  }
  MPI_Finalize();
}
