
int main(){
  int a[10];
  int *p, (*q)[10], *x;
  int b[10][10];

  q = &a;
  q = b;
  x = &q[2][2];
  p = b[0];
}
