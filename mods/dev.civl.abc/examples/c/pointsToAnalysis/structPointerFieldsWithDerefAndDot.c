struct H {
  int *c;
  int *d;
};

struct T {
  int *a;
  int *b;
  struct H *h;
};

int main() {
  struct T t, *t2;
  struct H h;
  int a, b, c, d;
  
  t.h = &h;
  t2 = &t;
  (*t2).a = &a;
  (*t2).b = &b;
  (*((*t2).h)).c = &c;
  (*((*t2).h)).d = &d;
}
