#include <complex.h>
#include <assert.h>

int main(void) {
  double _Complex x = 3.0+4.0i;
  assert(cabs(x) == 5.0);

}


