#include <civlc.cvh>

int y[2] = {1, 2};

int main () {
  int *p, *q;

  p = &y[0];
  q = p;

#pragma omp parallel for shared(p, q)
  for (int i=0; i<10; i++)
  {  
    *q = 1;
    *p = 2;
  }
  return 0;
}