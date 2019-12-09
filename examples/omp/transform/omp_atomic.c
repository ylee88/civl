#include <civlc.cvh>

#ifdef BAD
#define EXPR x
#else
#define EXPR 2
#endif

int main () {
  int x = 0;

#pragma omp parallel shared(x)
  { int v = 0;
    int n = 1;
    
    n = EXPR;
    
  #pragma omp atomic read
    v = x;

  #pragma omp atomic write
    x = n * 10;

  #pragma omp atomic update 
    x = n + x;
    
  #pragma omp atomic capture 
    v = x = x * 10;
  
  }
  return 0;
}