void f(int * p, int * q) {

}

int main() {
  int * p; int * q;

  int a, b;
  p = &a;
  q = p;
  q = &b;
  f(p, q);
}
