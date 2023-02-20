struct T {
  int *a;
  int *b;
  struct H {
    int *c;
    int *d;
  } h;
};
  
int main() {
  struct T t, *t2;
  int a, b, c, d;
  
  t.a = &a;
  t.b = &b;
  t.h.c = &c;
  t.h.d = &d;
  t2 = &t;
}
