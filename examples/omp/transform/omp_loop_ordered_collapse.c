#include <omp.h>
#include <stdio.h>

int main() {
  int i;
  int ctr = 0;
  int a[6] = {0,0,0,0,0,0};
  
  #pragma omp parallel for collapse(2) ordered
  for(int n=1; n < 3; n++)
    for (i=2; i<8; i+=2) {
      int v = 0;
//      ctr += 1;
      #pragma omp ordered
      {
        v = ctr + 1;
        ctr = v;
      }
      a[(n-1)*3 + i/2 - 1] = v;
//    printf("%d: %d, %d\n", omp_get_thread_num(), n, i);
    }
    
  for (int i=0; i<6; i++) {
    $assert(a[i] == i+1);
    printf("%d, ", a[i]);
  }
  printf("\n");
  return 0; 
}
