int main() {
  int a, b, c;
 int * p, *q;
 
 p = &a; p = &b;
 q = p; q = &c;
}
