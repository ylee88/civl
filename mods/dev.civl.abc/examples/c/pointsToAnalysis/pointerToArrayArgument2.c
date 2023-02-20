int *p, *q, *r;

int f(int *a) {
  p = a + 1;
  q = a + 2;
  r = a + *p;
}

int main() {
  int a[10];
  
  f(a + 1);
}
