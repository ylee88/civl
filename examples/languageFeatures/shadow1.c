#include <assert.h>
int main() {
  int a = 0;
  for (int i = 0; i < 10; i++) {
    int i = 7;
    a += i;
  }
  assert(a==70);
}
