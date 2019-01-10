#include<assert.h>

int main() {
  int x = 0;
  int y = 0;
#pragma omp parallel sections
  {
    #pragma omp section 
    {
      #pragma omp atomic read
      y = x;
    }
    #pragma omp section 
    {
      #pragma omp atomic write
      x = 1 + 1;
    }
  }
  assert (y == 0 || y == 2);
}
