#include <civlc.cvh>


int main () {
  int x[4] = {1, 2, 3, 4};
  int y = -1;

#pragma omp parallel sections firstprivate(y) shared(x)
{
{
  x[0] = x[1];
}
#pragma omp section
{
  x[2] = x[3];
}
#pragma omp section
{
  y = x[2];
}
}
  return 0;
}