#include<assert.h>
int main() {
  int a[32], b[16];
  int * p, * q, * r;
  
  p = a;
  q = p + 16;
  r = b;
#pragma omp simd safelen(9)
  for (int i = 0; i < 8; i++) {
    p[i] = 0;          // p[i] p[i'], p[i] p[i'+8]   |i-i'|<9
    p[i + 8] = 0;
    q[i] = 0;
    q[i + 8] = 0;
    r[i] = 0;
    r[i + 8] = 0;
  }

  for (int i = 0; i < 32; i++)
    assert(a[i] == 0);
  for (int i = 0; i < 16; i++)
    assert(b[i] == 0);
}
