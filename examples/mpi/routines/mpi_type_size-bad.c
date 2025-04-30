#include <mpi.h>
#include <assert.h>

void test_builtin_types() {
  int size1, size2, size3, size4;

  MPI_Type_size(MPI_INT, &size1);
  MPI_Type_size(MPI_FLOAT, &size2);
  MPI_Type_size(MPI_DOUBLE, &size3);
  MPI_Type_size(MPI_CHAR, &size4);

  $choose {
    $when ($true)
      assert(size1 != sizeof(int));

    $when ($true)
      assert(size2 != sizeof(float));

    $when ($true)
      assert(size3 != sizeof(double));

    $when ($true)
      assert(size3 != sizeof(char));
  }
}


int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    test_builtin_types();

    MPI_Finalize();
    return 0;
}
