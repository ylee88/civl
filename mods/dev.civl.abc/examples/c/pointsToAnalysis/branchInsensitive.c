int main() {
  int a, b, c;
  int * p, * q;
  
  if (a == b) {
    p = &a;
    q = &b;
  } else {
    p = &c;
    q = &a;
  }
}
