#include <civlc.cvh>
#include <stdio.h>

int main () {
  int sum0 = 0;
  int sum1 = 1000;
  int sub = 0;
  int prod = 10;

#pragma omp parallel num_threads(3) reduction(+: sum0, sum1) reduction(-: sub) reduction(*: prod)
  {
    sum0 += 1;
    sum1 += -100;
    sub -= 1;
    prod *= 10;
  }
  $assert(prod <= 10000);

  int max_val = -100;
  int min_val = 100;  

#pragma omp parallel num_threads(3) reduction(max: max_val) reduction(min: min_val)
  {
    if (max_val < sum0) max_val = sum0;
    if (min_val > sum0) min_val = sum0;
  }
  $assert(max_val == sum0);
  $assert(min_val == sum0);
  
  return 0;
}
