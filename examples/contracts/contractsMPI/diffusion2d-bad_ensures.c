#include <mpi.h>

#define FROMLEFT   0
#define FROMRIGHT  1
#define FROMBOTTOM 2
#define FROMTOP    3
#define comm MPI_COMM_WORLD

#pragma CIVL ACSL

double (*u)[];
double (*u_new)[];
double k;
int nxl, nyl;
int rank, nprocsx, nprocsy, left, right, top, bottom;

/*@ requires \valid((double (*)[nxl+2])u + (0 .. (nyl + 1)));
  @ requires \valid(((double (*)[nxl+2])u_new) + (0 .. (nyl + 1)));
  @ requires k > 0;
  @ requires 0 <= nxl && 0 <= nyl;
  @ assigns  u[1 .. nyl][1 .. nxl];
  @ ensures  \forall int i; 1 <= i && i <= nyl ==>
  @          ( \forall int j;  1 <= j && j <= nxl ==>
  @               u[i][j] == \old(u[i][j] +
  @               k*(u[i+1][j] + u[i-1][j] + 
  @               u[i][j+1] + u[i][j-1] - 4*u[i][j]))
  @          );
  @*/
void update() {
  double (* tmp)[];

  /*@ loop invariant 1 <= i && i <= nyl+1;
    @ loop invariant \forall int t; 1 <= t && t < i ==> 
    @                ( \forall int j0; 1 <= j0 && j0 <= nxl ==>
    @                    u_new[t][j0] == u[t][j0] + k*(u[t-1][j0] + u[t+1][j0] + u[t][j0+1] + u[t][j0-1] - 4 * u[t][j0])
    @                );
    @ loop assigns u_new[1 .. nyl][1 .. nxl], i;
    @*/
  for (int i = 1; i < nyl + 1; i++) {
    /*@ loop invariant 1 <= j && j <= nxl+1;
      @ loop invariant \forall int t; 1 <= t && t < j 
      @          ==> u_new[i][t] == u[i][t] + k*(u[i-1][t] + u[i+1][t] + u[i][t+1] + u[i][t-1] - 4 * u[i][t]);
      @ loop assigns u_new[i][1 .. nxl], j;
      @*/
    for (int j = 1; j < nxl + 1; j++) {
      u_new[i][j] = u[i][j] + 
        k*(u[i+1][j] + u[i-1][j] + 
           u[i][j+1] + u[i][j-1] - 4*u[i][j]);
    }
  }
  tmp = u;
  u = u_new;
  u_new = tmp;
}


/*@ requires \valid((double (*)[nxl+2])u + (0 .. (nyl + 1)));
  @ requires \valid((double (*)[nxl+2])u_new + (0 .. (nyl + 1)));
  @ requires 0 <= nxl && 0 <= nyl;
  @ \mpi_collective(MPI_COMM_WORLD, P2P):
  @   requires rank == \mpi_comm_rank;
  @   requires left == MPI_PROC_NULL || 
  @            (0 <= left && left < \mpi_comm_size && left != rank);
  @   requires right == MPI_PROC_NULL || 
  @            (0 <= right && right < \mpi_comm_size && right != rank);
  @   requires top == MPI_PROC_NULL || 
  @            (0 <= top && top < \mpi_comm_size && top != rank);
  @   requires bottom == MPI_PROC_NULL || 
  @            (0 <= bottom && bottom < \mpi_comm_size && bottom != rank);
  @   behavior hasTop:
  @     assumes  top != MPI_PROC_NULL;
  @     requires rank == \on(top, bottom) && top != left && top != right && top != bottom;
  @     requires nxl == \on(top, nxl);
  @     assigns  u[0][1 .. nxl];
  @     ensures  \forall int i; 1 <= i && i <= nxl ==> u[0][i] == \on(top, u[nyl][i]);
  @   behavior hasBottom:
  @     assumes  bottom != MPI_PROC_NULL;
  @     requires rank == \on(bottom, top) && bottom != left && bottom != right && bottom != top;
  @     requires nxl == \on(bottom, nxl);
  @     assigns  u[nyl+1][1 .. nxl];
  @     ensures  \forall int i; 1 <= i && i <= nxl ==> u[nyl+1][i] == \on(bottom, u[1][i]);
  @   behavior hasLeft:
  @     assumes  left != MPI_PROC_NULL;
  @     requires rank == \on(left, right) && left != right && left != bottom && left != top;
  @     requires nyl == \on(left, nyl);
  @     assigns  u[0][1 .. nyl];
  @     ensures  \forall int i; 1 <= i && i <= nyl ==> u[i][0] == \on(left, u[i][nxl]);
  @   behavior hasRight:
  @     assumes  right != MPI_PROC_NULL;
  @     requires rank == \on(right, left) && right != left && right != top && right != bottom;
  @     requires nyl == \on(right, nyl);
  @     assigns  u[nxl+1][1 .. nyl];
  @     ensures  \forall int i; 1 <= i && i <= nyl ==> u[i][nxl+1] == \on(right, u[i][0]);
  @*/
void exchange() {
  // sends top border row, receives into bottom ghost cell row
  MPI_Sendrecv(&u[1][1], nxl, MPI_DOUBLE, top, FROMBOTTOM, &u[nyl+1][1], nxl, 
               MPI_DOUBLE, bottom, FROMBOTTOM, comm, MPI_STATUS_IGNORE);
  // sends bottom border row, receives into top ghost cell row
  MPI_Sendrecv(&u[nyl][1], nxl, MPI_DOUBLE, bottom, FROMTOP, &u[0][1], nxl, 
               MPI_DOUBLE, top, FROMTOP, comm, MPI_STATUS_IGNORE);

  if (nyl > 0) {
    double sendbuf[nyl];
    double recvbuf[nyl];
    
    // sends left border column, receives into temporary buffer
    /*@ loop invariant 0 <= i && i <= nyl;
      @ loop invariant \forall int t; 0 <= t && t < i 
      @                 ==> sendbuf[t] == u[t+1][1];
      @ loop assigns sendbuf[0 .. nyl-1], i;
      @*/
    for (int i = 0; i < nyl; i++) sendbuf[i] = u[i+1][1];
    MPI_Sendrecv(sendbuf, nyl, MPI_DOUBLE, left, FROMRIGHT, recvbuf, nyl, 
		 MPI_DOUBLE, right, FROMRIGHT, comm, MPI_STATUS_IGNORE);
    // copies temporary buffer into right ghost cell column
    if (right != MPI_PROC_NULL) {
      /*@ loop invariant 0 <= i && i <= nyl;
	@ loop invariant \forall int t; 0 <= t && t < i ==>
	@                   recvbuf[t] == u[t+1][nxl+1];
	@ loop assigns u[1 .. nyl][nxl + 1], i;
	@*/
      for (int i = 0; i < nyl; i++) u[i+1][nxl+1] = recvbuf[i];
    }
    // sends right border column, receives into temporary buffer
    /*@ loop invariant 0 <= i && i <= nyl;
      @ loop invariant \forall int t; 0 <= t && t < i ==>
      @                   sendbuf[t] == u[t+1][nxl];
      @ loop assigns   sendbuf[0 .. nyl-1], i;
      @*/
    for (int i = 0; i < nyl; i++) sendbuf[i] = u[i+1][nxl];
    MPI_Sendrecv(sendbuf, nyl, MPI_DOUBLE, right, FROMLEFT, recvbuf, nyl, 
		 MPI_DOUBLE, left, FROMLEFT, comm, MPI_STATUS_IGNORE);
    // copies temporary buffer into left ghost cell column
    if (left != MPI_PROC_NULL) {
      /*@ loop invariant 0 <= i && i <= nyl;
	@ loop invariant \forall int t; 0 <= t && t < i ==>
	@                   recvbuf[t] == u[t+1][0];
	@ loop assigns   u[1 .. nyl][0], i;
	@*/
      for (int i = 0; i < nyl; i++) u[i+1][0] = recvbuf[i];
    }
  }
}


