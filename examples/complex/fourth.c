/* Sums the 4-th roots of unity using MPI with 4 procs. */
#include <assert.h>
#include <complex.h>
#include <mpi.h>

int nprocs, rank;

int main(void) {
  MPI_Init(NULL, NULL);
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  assert(nprocs == 4);
  long double _Complex x = 1.0il; // primitive 4-th root of unity
  long double _Complex y = 1.0l;
  for (int i=0; i<rank; i++)
    y *= x;
  // now y = i^rank
  long double _Complex s;
  MPI_Allreduce(&y, &s, 1, MPI_C_LONG_DOUBLE_COMPLEX, MPI_SUM, MPI_COMM_WORLD);
  assert(s == 0.0l);
  MPI_Finalize();
}
