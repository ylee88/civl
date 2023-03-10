#include <civlc.cvh>

// even to avoid data race
#ifdef BAD
#define N 4*2+1
#else
#define N 4*2
#endif

int main () {
  int len = N; 
  int arr[N];
  
  for (int i=0; i<len; i++)
    arr[i] = i;

#pragma omp parallel for firstprivate(len) shared(arr)
  for (int i=0; i<len; i+=2)
    arr[(i+1)%len] = arr[i];
  return 0;
}