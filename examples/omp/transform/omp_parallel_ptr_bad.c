#include <civlc.cvh>

// even to avoid data race
#ifdef BAD
#define N 4*2+1
#else
#define N 4*2
#endif

int len = N;
int gArr[N];

int main () {
  int *p, *q;
  
  p = &gArr[0];
  q = p + (len -1);
  for (int i=0; i<len; i++)
    gArr[i] = i;

#pragma omp parallel for firstprivate(len, p, q)
  for (int i=2; i<len; i+=2) {
    *q = *p;
    p = q-1;
    q = q-2;
  }
  return 0;
}