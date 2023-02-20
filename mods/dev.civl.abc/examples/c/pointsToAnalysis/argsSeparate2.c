void f(int * p, int * q) {

}

int main() {
  int * p, * q, * r;
  int a, b, c;

  p = &a;
  q = &b;
  f(p, q);
  r = &c;
  f(p, r);
}
