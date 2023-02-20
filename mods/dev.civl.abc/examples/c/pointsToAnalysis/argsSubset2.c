int b;

void f(int * p, int * q) {
  f(p, &b);
}

int main() {
  int * p, * q;
  int a;
  p = &a;
  q = p;
  f(p, q);
}
