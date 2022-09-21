#include <cuda.h>

__global__ void simple1(void) {
}

__global__ void simple2(void);

__global__ void simple2(void) {
	
}

int main(void) {
	simple1<<<1, 1, 0>>>();
	simple2<<<1, 1, 0>>>();
	return 0;
}


