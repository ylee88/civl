#include <assert.h>
#include <math.h>

int main(void) {
  double x = floor(2.5);
  assert(x == 2.0);
  double y = floor(-2.5);
  assert(y == -3.0);
  assert(ceil(2.5) == 3.0);
  assert(ceil(-2.5) == -2.0);
  assert(floor(2.0) == 2.0);
  assert(floor(-2.0) == -2.0);
  assert(ceil(2.0) == 2.0);
  assert(ceil(-2.0) == -2.0);

  x = 2.71828;
  int a = x;
  assert(a == 2);
  x = -2.71828;
  a = x;
  assert(a == -2);
}
