#include <assert.h>
struct S {
  int a;
} ops[1];
int main(void) {
  ops->a=1;
  assert(ops->a==1);
}
