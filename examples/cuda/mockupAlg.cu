#include <cuda.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>

__global__ void kernel_1(float* A, float* C, int numElements) {
  int lane = threadIdx.x % warpSize;
  int thisWarpSize = warpSize;
  if (threadIdx.x - lane + warpSize > blockDim.x) {
    thisWarpSize = ((blockDim.x - 1) % warpSize) + 1;
  }

  int i = blockDim.x * blockIdx.x + threadIdx.x;
  int warpStart = i - lane;
  //printf("%d,%d - i: %d, warpStart: %d, thisWarpSize: %d\n", blockIdx.x, threadIdx.x,i, warpStart, thisWarpSize);
  int remainingElements = numElements;

  while (remainingElements > 1) {
    //printf("%d,%d - remainingElements: %d\n", blockIdx.x, threadIdx.x, remainingElements);
    if (remainingElements < numElements) {
      __syncthreads();
      //printf("%d,%d - entering barrier\n", blockIdx.x, threadIdx.x);
      //$cuda_barrier($kernel, _cuda_kid, _cuda_thread_barrier);
      //printf("%d,%d - exiting barrier\n", blockIdx.x, threadIdx.x);
    }
        
    if (warpStart + 1 < remainingElements) {
      float val = i < numElements ? A[i] : 0;
          
      for (int offset = warpSize/2; offset > 0; offset /= 2) {
        //float tmp = $cuda__shfl_down_sync(val, offset, $lane);
        float tmp = __shfl_down_sync(0xFFFFFFFF, val, offset);
        if (lane + offset < thisWarpSize) {
          val += tmp;
        }
      }

      if (i < numElements) {
        A[i] = val;
        //printf("%d,%d - writing A[%d]: %f\n", blockIdx.x, threadIdx.x, i, val);
      }
    }

    i *= warpSize;
    //warpStart *= warpSize;
    remainingElements = ((remainingElements - 1) / warpSize) + 1;
  }
      
  if (i == 0) {
    *C = A[0];
  }
}

/*
__global__ void kernel_1(float* A, float* C, int numElements) {
  if (blockDim.x * blockIdx.x + threadIdx.x == 0) {
    *C = 0;
    for (int i = 0; i < numElements; i++) {
      *C += A[i];
    }
  }
}
*/
$input int N = 64;

int main() {
  float A[N];
  float sum = 0;
  
  for (int i = 0; i < N; i++) {
    A[i] = i;
    sum += A[i];
    printf("sum: %f, A[i]: %f\n", sum, A[i]);
  }

  int size = N * sizeof(float);
  int numBlocks = 1;
  //int numThreads = N%2 == 0? N/2 : (N+1)/2;
  int numThreads = N;

  float* cuda_A;
  cudaMalloc((void **)&cuda_A, size);
  cudaMemcpy(cuda_A, A, size, cudaMemcpyHostToDevice);

  float* cuda_C;
  cudaMalloc((void **)&cuda_C, sizeof(float));

  kernel_1<<<numBlocks, numThreads>>>(cuda_A, cuda_C, N);
  //kernel_1<<<1, N>>>(cuda_A, cuda_C, N);
  
  // Checking correctness
  float* C = (float *)malloc(sizeof(float));
  
  cudaMemcpy(C, cuda_C, sizeof(float), cudaMemcpyDeviceToHost);

  printf("sum: %f, C: %f", sum, *C);
  
  assert(*C == sum);
  
  free(C);
  
  cudaFree(cuda_A); 
  cudaFree(cuda_C);

}