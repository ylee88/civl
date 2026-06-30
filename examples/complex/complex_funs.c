#include <complex.h>
#include <assert.h>

int main(void) {
  double _Complex x = 3.0 + 4.0i;
  assert(cabs(x) == 5.0);
  float _Complex x_f = 3.0f + 4.0if;
  assert(cabsf(x_f) == 5.0f);
  long double _Complex x_l = 3.0L + 4.0IL;
  assert(cabsl(x_l) == 5.0L);
  assert(conj(x) == 3.0 - 4.0i);
}
