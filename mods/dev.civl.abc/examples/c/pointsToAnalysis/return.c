int c;

int * f() {
  int * p = &c;
  return p;
} 

int * g() {
  int x;
  int * p = &x;
  return p;
} 

int main() {
  int * p =f();
  int * q = &c;
  int * r = g();
}
