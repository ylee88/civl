int main() {
  int a, b, c;
  int **p, *q;
  
  q = &a;
  q = &b;
  p = &q;
  *p = &c;
}
