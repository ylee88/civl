#include <mpi.h>
#include <stdlib.h>
#include <assert.h>

void test_sum_int() {
    int inbuf[3] = {1, 2, 3};
    int inoutbuf[3] = {4, 5, 6};
    MPI_Reduce_local(inbuf, inoutbuf, 3, MPI_INT, MPI_SUM);

    assert(inoutbuf[0] == 5); // 1 + 4
    assert(inoutbuf[1] == 7); // 2 + 5
    assert(inoutbuf[2] == 9); // 3 + 6
}

void test_max_float() {
    float inbuf[2] = {1.5, 7.0};
    float inoutbuf[2] = {2.0, 5.0};
    MPI_Reduce_local(inbuf, inoutbuf, 2, MPI_FLOAT, MPI_MAX);

    assert(inoutbuf[0] == 2.0f); // max(1.5, 2.0)
    assert(inoutbuf[1] == 7.0f); // max(7.0, 5.0)
}

void test_product_double() {
    double inbuf[4] = {1.0, 2.0, 3.0, 4.0};
    double inoutbuf[4] = {2.0, 2.0, 2.0, 2.0};
    MPI_Reduce_local(inbuf, inoutbuf, 4, MPI_DOUBLE, MPI_PROD);

    assert(inoutbuf[0] == 2.0);  // 1.0 * 2.0
    assert(inoutbuf[1] == 4.0);  // 2.0 * 2.0
    assert(inoutbuf[2] == 6.0);  // 3.0 * 2.0
    assert(inoutbuf[3] == 8.0);  // 4.0 * 2.0
}

void test_min_int() {
    int inbuf[2] = {100, -50};
    int inoutbuf[2] = {99, -30};
    MPI_Reduce_local(inbuf, inoutbuf, 2, MPI_INT, MPI_MIN);

    assert(inoutbuf[0] == 99);  // min(100, 99)
    assert(inoutbuf[1] == -50); // min(-50, -30)
}

int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    test_sum_int();
    test_max_float();
    test_product_double();
    test_min_int();


    MPI_Finalize();
    return 0;
}
