int main() {
  int a, b, c;
  int *p, **p2, *q, *r;

  p = &a;
  p2 = &p;
  q = *p2;
  *p2 = &b;
  r = &c;
}
