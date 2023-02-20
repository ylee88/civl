#include<assert.h>

struct T {
  int x;
  int y;
  struct T2 {
    int x;
    int y;
  } z;
};

int main() {
  struct T t = {.y=2, {.x=3, 4, 5}};

  assert(t.x == 0 && t.y == 2 && t.z.x == 3 && t.z.y == 5);
  return t.z.x;
}
