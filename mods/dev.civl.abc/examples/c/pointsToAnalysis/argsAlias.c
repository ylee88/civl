void f(int * p, int * q) {

}

int main() {
  int * p; int * q;
  int a, b;
  p = &a;
  q = &a;
  f(p, q);
}
