struct T {int x;};
  
int main() {
  struct T t[10];
  struct T * p;
  int i, *q, *r;
  
  p = t;
  q = &(p->x);
  r = &((p + i)->x);
}
