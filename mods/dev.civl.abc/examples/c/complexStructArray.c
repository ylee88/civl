#include<stdlib.h>
#include<assert.h>

int main() {
  struct T {
    int (*x)[10];
  } ** a;
  
  a = (struct T **)malloc(sizeof(struct T *));
  a[0] = (struct T *)malloc(sizeof(struct T));
  a[0][0].x = (int (*)[10])malloc(sizeof(int[10]));
  a[0][0].x[0][0] = 9;

  assert(a[0][0].x[0][0] == 9);
}
