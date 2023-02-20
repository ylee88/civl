int main() {
  int a, b, c;
  int * p, **p2, ***p3;

  p = &a; p = &b;
  p2 = &p; p3 = &p2;
}
