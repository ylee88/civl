#include <stdio.h>
int x=1;
int main() {
  int y=x;
  int x=2;
  printf("%d %d\n", x, y);
}
