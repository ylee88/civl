#include<assert.h>

int main() {
  int x = 0;
  int y = 0;
#pragma omp parallel sections
  {
    #pragma omp section 
    {
      #pragma omp atomic update
      x++;
    }
    #pragma omp section 
    {
      #pragma omp atomic update 
      x = x + y;
    }
    #pragma omp section 
    {
      #pragma omp atomic update
      x += y;
    }
    #pragma omp section 
    {
      #pragma omp atomic update
      x = y + x;
    }
  }
  assert (x == 0 || x == 1);
}
