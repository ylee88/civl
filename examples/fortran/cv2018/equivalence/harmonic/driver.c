#include <math.h>
#include <stdio.h>
$input double IN;
$output double OUT;
$assume (IN > 0);

void F_HARMONIC(double, double, double, double, double, double *);

int main(){
  double x[5],f,v;
  double alpha,beta,gamma,beta2,beta3;

  v = IN;
  x[0] = sin(v);
  x[1] = cos(v);
  x[2] = sqrt(v);
  x[3] = log(v);
  x[4] = sin(1./v);
  alpha = x[0];
  beta = x[1];
  gamma = x[2];
  beta2 = x[3];
  beta3 = x[4];
  F_HARMONIC(alpha,beta,gamma,beta2,beta3,&f);
  printf("f = %g\n",f);
  OUT = f;
}
