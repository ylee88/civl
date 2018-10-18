#include<assert.h>

struct T {
  struct {
    int x;
    int y;
  };
  int z;
};

int main() {
  struct T a = {.x = 1, .y = 2, .z = 3};

  assert(a.x == 1 && a.y == 2 && a.z == 3);
}
