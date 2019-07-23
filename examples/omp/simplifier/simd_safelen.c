#include<assert.h>
int main() {
  int a[16];
#pragma omp simd safelen(2)
  for (int i = 0; i < 4; i++) {
    a[i] = 0;
    a[i + 4] = 0;
    a[i + 8] = 0;
    a[i + 12] = 0;
  }

  for (int i = 0; i < 16; i++)
    assert(a[i] == 0);
}
