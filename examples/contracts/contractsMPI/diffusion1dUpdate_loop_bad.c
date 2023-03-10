#include<mpi.h>
#include<civlc.cvh>

#pragma CIVL ACSL
#define DATA_LIMIT 1024

int nxl;
double * u, * u_new, k;


/*@ requires \valid(u + (0 .. (nxl + 1)));
  @ requires \valid(u_new + (0 .. (nxl + 1)));
  @ requires k > 0 && nxl > 0;
  @ assigns  u[1 .. nxl];
  @ ensures  \forall int i; 0< i && i <= nxl
  @           ==> 
  @          u[i] == \old(u[i] + k*(u[i+1] + u[i-1] - 2*u[i]));
  @*/
void update() {
  /*@ loop invariant 1 <= i && i <= nxl + 1;
    @ loop invariant \forall int j; 1 <= j && j < i ==> 
    @        u_new[j] == u[j] + k*(u[j+1] + u[j-1] - 2*u[j]);
    @*/
  for (int i = 1; i <= nxl; i++)
    u_new[i] = u[i] + k*(u[i+1] + u[i-1] - 2*u[i]) + 1;

  double * tmp = u_new; u_new=u; u=tmp;
}
