#include <assert.h>
union U {
  int x;
  int y;
};

int main() {
  // override cases:
  union U u0 = {.x = 1, .y = 2};  
  union U u = {.x = 1, .x = 2};  
  
  assert(u.y == 2);  // pass
  assert(u.x == 2);  // not pass, inconsist
}
