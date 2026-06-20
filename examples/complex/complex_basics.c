#include <assert.h>
#include <complex.h>
#include <stdbool.h>

double _Complex x = 1+2.0i;

int main(void) {
  assert(x == 1 + 2.i);
  assert(x == 1 + 2*I);
  assert(x == 1 + 2*_Complex_I);
  assert(x == CMPLX(1.0, 2.0));
  assert(creal(x) == 1.0);
  assert(cimag(x) == 2.0);
  assert(x);
  assert(x!=0);
  assert(2*x == 2 + 4.0i);

  // Check implicit conversions to _Bool...
  double _Complex y1 = 3.14;
  double _Complex y2 = 3.14i;
  double _Complex y3 = 0;
  if (y1)
    assert(true);
  else
    assert(false);
  if (y2)
    assert(true);
  else
    assert(false);
  if (y3)
    assert(false);
  else
    assert(true);
  while (y3) {
    assert(false);
  }

  // Check other conversions...
  float _Complex x_f = x;
  assert(x_f == 1.0f + 2.if);
  assert(crealf(x_f) == 1.0f);
  assert(cimagf(x_f) == 2.0f);
  assert(x_f == CMPLXF(1, 2));
  double a = x;
  assert(a==1);
  long double _Complex x_l = x;
  assert(x_l == (long double)1.0 + ((long double)2.0)*I);
}
