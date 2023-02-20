#include<assert.h>

int main() {
  int a[10] = {0};
  int y = 0;
#pragma omp parallel sections
  {
#pragma omp section 
    {
#pragma omp atomic read
      y = a[0];
    }
#pragma omp section 
    {
#pragma omp atomic write
      a[0] = 1 + 1;
    }
  }
  assert (y == 0 || y == 2);
}
