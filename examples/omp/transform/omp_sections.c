#include <civlc.cvh>
#ifndef BAD
#define OFFSET 0
#else
#define OFFSET 1
#endif

int main () {
  int x[4] = {1, 2, 3, 4};
  int y = 10;

#pragma omp parallel sections firstprivate(y) shared(x)
{
{
  x[0] = x[0 + OFFSET] + y;
}
#pragma omp section
{
  x[1] = x[1 + OFFSET] + y;
}
#pragma omp section
{
  x[2] = x[2 + OFFSET] + y;
}
#pragma omp section
{
  x[3] = x[3 + OFFSET] + y;
}
}
  return 0;
}