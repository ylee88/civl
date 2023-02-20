int main() {
  int a, b;
  int * p, **p2, **p3;

  p = &a; 
  *p2 = (b++ + 3) * sizeof(int) + p;
  p3 = (b++ + 3) * sizeof(int) + &p;
}
