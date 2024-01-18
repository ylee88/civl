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

  if (warpStart + 1 < numElements) {
    float val = i < numElements ? A[i] : 0;
    for (int offset = warpSize/2; offset > 0; offset /= 2) {
      float tmp = __shfl_down_sync(0, val, offset);
      //float tmp = i + offset < numElements ? A[i + offset] : 0;
      if (lane + offset < thisWarpSize) {
        val += tmp;
      }
    }

    if (i < numElements) {
      A[i] = val;
    }
  }

  __syncthreads();
  if (threadIdx.x == 0) {
    int blockEnd = blockDim.x * (blockIdx.x + 1);
    if (blockEnd > numElements) {
      blockEnd = numElements;
    }
    for (int j = i + warpSize; j < blockEnd; j += warpSize) {
      A[i] += A[j];
    }
    atomicAdd(C + blockIdx.x, 1);
  }

  if (i == 0) {
    C[0] = A[0];
    for (int j = 1; j < gridDim.x; j++) {
      while(atomicAdd(C+j,0) == 0) {}
      C[0] += A[j * blockDim.x];
    }
  }
}

$input int N = 64;
$input float A[N];

$input int threadsPerBlock = N%2 == 0 ? N/2 : (N+1)/2;
int numBlocks = (N-1)/threadsPerBlock + 1;

int main() {
  int size = N * sizeof(float);

  float* cuda_A;
  cudaMalloc((void **)&cuda_A, size);
  cudaMemcpy(cuda_A, A, size, cudaMemcpyHostToDevice);

  float C[numBlocks];
  for (int i = 0; i < numBlocks; i++) {
    C[i] = 0;
  }

  float* cuda_C;
  cudaMalloc((void **)&cuda_C, numBlocks * sizeof(float));
  cudaMemcpy(cuda_C, C, numBlocks * sizeof(float), cudaMemcpyHostToDevice);

  kernel_1<<<numBlocks, threadsPerBlock>>>(cuda_A, cuda_C, N);
  
  cudaMemcpy(C, cuda_C, sizeof(float), cudaMemcpyDeviceToHost);

  float sum = 0;
  for(int i = 0; i < N; i++)
    sum += A[i];
  
  $assert(*C == sum);
  
  cudaFree(cuda_A); 
  cudaFree(cuda_C);
}