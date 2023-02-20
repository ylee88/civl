int *p, *q;

int f(int *a) {
  p = a + 1;
  q = a + 2;
}

int main() {
  int a[10];
  
  f(a);
}
