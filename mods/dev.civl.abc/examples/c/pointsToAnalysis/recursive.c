struct T {
  int x;
  int y;
} t;

void f(int ** p, int ** q) {
  *q = &t.y;
  f(q, p);
}

void g(int ** p, int ** q) {
  *p = &t.x;
  f(p, q);
}

int main() {
  int *x, *y;
  
  g(&x, &y);
}
