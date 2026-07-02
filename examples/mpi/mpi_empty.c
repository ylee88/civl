#include <mpi.h>

// Because this program includes mpi.h, CIVL requires that the number
// of MPI processes or an upper bound on the number of processes be
// specified.  This can happen on the command line; type "civl help"
// for details.  Alternatively, this information can be specified
// programmatically as follows:

// To specify an upper bound:
// $input int _mpi_nprocs_hi = 4;

// To specify an exact number:
// $input int _mpi_nprocs = 3;

// You can also specify a lower bound:
// $input int _mpi_nprocs_lo = 2;

int main(void) {
}
