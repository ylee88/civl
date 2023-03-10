void f(double *a, double (*b)[10]) {
#pragma omp parallel for private(a, b)
  for (int i = 0; i < 9; i++) {
    a[i] = b[i][i];
  }
}

int main() {
  double A[10][10] = {0};
  double *p;

  p = &A[0][1];
  f(p, A);
}
