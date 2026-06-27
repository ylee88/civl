#include <assert.h>
#include <complex.h>
#include <math.h>

int main(void) {
  double s = sqrt(2.0);
  double _Complex x = s + s*I;
  double _Complex y = -s + s*I;
  double _Complex r;
  
  r = x += y;
  assert(x == 2*s*I);
  assert(y == -s + s*I);
  assert(r == x);

  r = x -= y;
  assert(x == s + s*I);
  assert(y == -s + s*I);
  assert(r == x);

  r = x *= y;
  assert(x == -4.0);
  assert(y == -s + s*I);
  assert(r == x);

  r = x /= y;
  assert(x == s + s*I);
  assert(y == -s + s*I);
  assert(r == x);
}
