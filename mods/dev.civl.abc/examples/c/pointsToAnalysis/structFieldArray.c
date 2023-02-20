struct T {
  int a[10][10];
  int b[10];
  struct T * t;
};

int main() {
  struct T s, s2;
  int *p, (*q)[10];
  
  s.t = &s2;
  p = s.b;
  q = s.a;
  q = s.t->a;
  p = s.t->b;
}

