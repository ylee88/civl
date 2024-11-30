#include <assert.h>
int a[];
int a[] = {1, 2};
int main(void) {
  assert(a[0] == 1 && a[1] == 2 && a[2] == 3);
}
