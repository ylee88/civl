#include<assert.h>
struct T {
  int x;
  int y;
};

int main() {
  struct T t = {1, 0};
  
#pragma omp parallel sections
  {
    #pragma omp section 
    {
      #pragma omp atomic read
      t.y = t.x;
    }
    #pragma omp section 
    {
      #pragma omp atomic write
      t.x = 1 + 1;
    }
  }
  assert (t.y == 1 || t.y == 2);
}
