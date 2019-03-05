#include<mpi.h>
#pragma CIVL ACSL

struct T {
  int *x;
};
int * y;
int i[10];
int (*u)[100];
int v[100][100][100];

/*@ 
  @ requires \valid(x + (0 .. 10));
  @ requires \valid(u + (0 .. 10));
  @ requires \valid(y + (0 .. 10));
  @ assigns  u[0 .. 2][0 .. 2], v[0 .. 2][0][0 .. 2], 
  @          x;
  @ ensures  *y == 0;
  @ behavior test:
  @   assumes \true;
  @   assigns *(y + (0..2));
  @   ensures *y > 0;
  @*/
int foo(struct T * x) {
  return x->x[0];
}

/*@ 
  @ requires \true;
  @*/
int g() {
  void * x;
  
  return foo(x);
}
