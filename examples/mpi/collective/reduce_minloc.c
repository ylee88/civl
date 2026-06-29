#include <assert.h>
#include <mpi.h>

struct Pair {
  int val;
  int rank;
};

int main(void) {
  MPI_Init(NULL, NULL);
  int rank, nprocs;
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  int mynum = rank - nprocs/2;
  if (mynum < 0) mynum = -mynum;
  struct Pair mypair = { mynum, rank }, result;
  MPI_Reduce(&mypair, &result, 1, MPI_2INT, MPI_MINLOC, 0, MPI_COMM_WORLD);
  if (rank == 0) {
    assert(result.val == 0);
    assert(result.rank == (nprocs - nprocs%2)/2);
  }
  MPI_Finalize();
}
