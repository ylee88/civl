int c;
int *g;

int * f() {
  int ** p = &g;
  return *p;
} 

int * gg() {
  return &c;
} 

int main() {
  g = &c;
  int * p = f();
  int * q = &c;
  int * r = gg();
}
