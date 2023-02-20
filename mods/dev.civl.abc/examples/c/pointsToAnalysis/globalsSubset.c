int * p, * q;
 
void f() {
  int a, b;
  p = &a;
  q = p;
  q = &b;
}
  
int main() {
  f();
}
