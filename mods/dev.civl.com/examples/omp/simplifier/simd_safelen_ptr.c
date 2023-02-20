#include<assert.h>
int main() {
  struct T {
    int a[32];
    int b[16];
  } t;
  int * p, * q, * r;
  
  p = t.a;
  q = p + 16;
  r = t.b;
#pragma omp simd safelen(8)
  for (int i = 0; i < 8; i++) {
    p[i] = 0;          
    p[i + 8] = 0;
    q[i] = 0;
    q[i + 8] = 0;
    r[i] = 0;
    r[i + 8] = 0;
  }

  for (int i = 0; i < 32; i++)
    assert(t.a[i] == 0);
  for (int i = 0; i < 16; i++)
    assert(t.b[i] == 0);
}
