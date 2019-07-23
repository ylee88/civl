#define N 10

int main() {
  int a[N][N], b[N];
  int *p, *q;
  
  p = &a[0][0];
  q = &b[0];
#pragma omp parallel for shared(a, b)
  for (int i = 0; i < N; i++) {
    p[i] = q[i];
  }
}
