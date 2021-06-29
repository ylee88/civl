#include "include_order_2.h"

__global__ void test_kernel(int* data, int numv)
{
  int idx = threadIdx.x + blockIdx.x * blockDim.x;
  if (idx < numv) {
    data[0]++;
  }
}