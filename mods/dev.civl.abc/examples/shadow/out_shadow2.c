#include <stdio.h>
int main() {
  int x=1;
  {
    int _x_1=2;
    {
      int _x_2=3;
      printf("%d", _x_2);
    }
    printf("%d", _x_1);
  }
  printf("%d", x);
}
