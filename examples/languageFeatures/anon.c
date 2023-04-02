#include <stdio.h>
struct S {
  int x;
  int;
  int :10;
  int y;
};

int main() {
  struct S s;
  printf("%zu %zu %zu\n", sizeof(int), sizeof(struct S), sizeof(s));
  s.x=10;
  s.y=20;
}
