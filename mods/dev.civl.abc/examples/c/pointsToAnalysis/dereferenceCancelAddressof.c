int main() {
  int *p;
  int *x, a;
  
  x = &a;
  *(&p) = *(&x);
}
