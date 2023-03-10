
#include <mpi.h>

int rank;

int main() {
	MPI_Init(NULL, NULL);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Finalize();
}
