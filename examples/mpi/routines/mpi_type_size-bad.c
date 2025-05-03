#include <mpi.h>
#include <assert.h>

void test_builtin_types() {
  int size1, size2, size3, size4;

  MPI_Type_size(MPI_INT, &size1);
  MPI_Type_size(MPI_FLOAT, &size2);
  MPI_Type_size(MPI_DOUBLE, &size3);
  MPI_Type_size(MPI_CHAR, &size4);

  int x = $choose_int(4);

  switch(x) {
  case 0:
    assert(size1 != sizeof(int));
  case 1:
    assert(size2 != sizeof(float));
  case 2:
    assert(size3 != sizeof(double));     
  case 3:
    assert(size3 != sizeof(char));
  }
}


int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    test_builtin_types();

    MPI_Finalize();
    return 0;
}
