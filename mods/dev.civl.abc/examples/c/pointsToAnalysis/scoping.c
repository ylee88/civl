int c;

void f(int * p, int * q, int * r) {
  int a, b;
  p = &a;
  q = &b;
  r = &c;
}

int main() {
  int *p, *q, *r;

  f(p, q, r);
}
