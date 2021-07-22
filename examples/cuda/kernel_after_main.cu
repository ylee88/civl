#include <civlc.cvh>
#include <stdlib.h>
#include <cuda.h>

$input int N = 3;
$assume (N >= 1);

$input int DATA[N];

$input int THREADS_PER_BLOCK = 2;
$input int NUM_BLOCKS = 2;

__global__ void test_kernel(int* data, int n);

int main ()
{
  // allocate and init two data arrays
  int* data = (int*) malloc(N * sizeof(int));
  for (int i = 0; i < N; i++) {
    data[i] = DATA[i];
  }

  int* d_data;
  cudaMalloc((void**) &d_data, N * sizeof(int));
  cudaMemcpy(d_data, data, N * sizeof(int), cudaMemcpyHostToDevice);

  test_kernel<<<NUM_BLOCKS, THREADS_PER_BLOCK>>>(d_data, N);

  cudaMemcpy(data, d_data, N * sizeof(int), cudaMemcpyDeviceToHost);
  cudaFree(d_data);
  free(data);
  return 0;
}

__global__ void test_kernel(int* data, int numv)
{
  int idx = threadIdx.x + blockIdx.x * blockDim.x;
  if (idx < numv) {
    data[0]++;
  }
}