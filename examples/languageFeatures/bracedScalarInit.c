#include <assert.h>

int main() {
  int x = (int){9};
  int *p = (int *){&x};

  assert (x == 9 && *p == 9);
}
