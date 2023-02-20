/* Tests pointer addition with a void* pointer, allowed in GNU-C */
#include <assert.h>

int main() {
  int x;
  void *p = &x;
  int *q = p + sizeof(int);
  assert(p == q - 1);
 }
 