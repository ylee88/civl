#include <mpi.h>
#include <assert.h>

void test_builtin_types() {
    int size;

    MPI_Type_size(MPI_INT, &size);
    assert(size == sizeof(int));

    MPI_Type_size(MPI_FLOAT, &size);
    assert(size == sizeof(float));

    MPI_Type_size(MPI_DOUBLE, &size);
    assert(size == sizeof(double));

    MPI_Type_size(MPI_CHAR, &size);
    assert(size == sizeof(char));
}


int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    test_builtin_types();

    MPI_Finalize();
    return 0;
}
