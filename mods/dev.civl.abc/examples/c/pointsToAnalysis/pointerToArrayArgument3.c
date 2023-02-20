int *p, *q, *r;

int f(int *a, int b[10]) {
  p = a + 1;
  q = b + 2;
  r = b + *p;
}

int main() {
  int a[10][10];
  int (*b)[10];
  
  b = a + 1;
  f(a[1] + 1, b[2] + 1);
}
