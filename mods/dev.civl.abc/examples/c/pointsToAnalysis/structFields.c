struct T {
  int a;
  int b;
  struct H {
    int c;
    int d;
  } h;
};

int main() {
  struct T t;
  int * p, * p2, * p3, * p4;
  void * p5;
  
  p = &t.a;
  p2 = &t.b;
  p3 = &t.h.c;
  p4 = &t.h.d;
  p5 = &t.h;
}
