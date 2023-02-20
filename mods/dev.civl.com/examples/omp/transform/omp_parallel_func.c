#include <civlc.cvh>

// even to avoid data race
#ifdef BAD
#define N 4*2+1
#else
#define N 4*2
#endif

int len = N;
int gArr[N];

int f(int val) {
  return ++val;
}

void g(int i, int val) {
  gArr[i] = f(val);
}

int main () {
  for (int i=0; i<len; i++)
    gArr[i] = i;
    
#pragma omp parallel for firstprivate(len) shared(gArr)
  for (int i=0; i<len; i+=2)
    g((i+1)%len, gArr[i]);
  return 0;
}