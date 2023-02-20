int main() {
  int a, b, c, d;
  int **p, **q, *y, *x;
  
  y = &a;
  x = &b;
  q = &y;
  p = &x;
  y = *p; // pts(x) subset of pts(y)
  *q = x;
  
}
