int * p, * q;
 
void f() {
  int a, b;
  p = &a;
  q = &a;
}
  
int main() {
  f();
}
