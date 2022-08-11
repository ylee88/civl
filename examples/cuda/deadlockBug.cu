#include <cuda.h>
#include <stdio.h>
#include <stdlib.h>
#include <civlc.cvh>

__global__ void test_kernel($gbarrier gb)
{
  //Added to avoid data race	
  $read_set_push();
  $write_set_push();

  $barrier barrier = $barrier_create($here, gb, threadIdx.x);
  $barrier_call_yield(barrier);
  $barrier_destroy(barrier);
  //Note that this is not printed
  printf("Thread %d got through barrier\n", threadIdx.x);

  //Added to avoid data race
  $read_set_pop();
  $write_set_pop();
}

int main(){
	$gbarrier gb = $gbarrier_create($here, 3);
	$gbarrier gb_dev = $gbarrier_create($here, 3);

	cudaMemcpy(gb_dev, gb, sizeof(struct _gbarrier), cudaMemcpyHostToDevice);
	//Barrier of size three will only be called by 2 threads
	test_kernel<<<1, 2>>>(gb_dev);
	//There is a bug where the second cudaMemcpy is required
	cudaMemcpy(gb, gb_dev, sizeof(struct _gbarrier), cudaMemcpyDeviceToHost);

	$gbarrier_destroy(gb);
	$gbarrier_destroy(gb_dev);
}
