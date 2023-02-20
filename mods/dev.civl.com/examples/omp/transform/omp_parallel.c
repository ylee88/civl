#include <civlc.cvh>

int main () {
  int x[10] = {1, 2, 3};
  int y = -1;

#pragma omp parallel for firstprivate(y) shared(x)
  for (int i=0; i<10; i+=1)
  { 
    y = i+y;
    x[i] = y;
  }
  $assert(y == -1);
  return 0;
}