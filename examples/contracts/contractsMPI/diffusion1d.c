#include<mpi.h>
#include<civlc.cvh>
#include<assert.h>

#pragma CIVL ACSL

int left, right, nxl, nx, rank, nprocs;
double * u, * u_new, k;

#ifdef WEAKER
#define DISTRI (0 <= left && left < \mpi_comm_rank || left == MPI_PROC_NULL) && \
    (\mpi_comm_rank < right && right < \mpi_comm_size || right == MPI_PROC_NULL)
#elif defined (WEAKEST)
#define DISTRI left != \mpi_comm_rank && (0 <= left && left < \mpi_comm_size || left == MPI_PROC_NULL) && \
    right != \mpi_comm_rank && (0 <= right && right < \mpi_comm_size ||  right == MPI_PROC_NULL)
#else
#define DISTRI ((0 <= left && left == \mpi_comm_rank-1) || left == MPI_PROC_NULL) && \
  ((\mpi_comm_rank+1 == right && right < \mpi_comm_size) || right == MPI_PROC_NULL)
#endif

/*@ \mpi_collective(MPI_COMM_WORLD, P2P):
  @   requires rank == \mpi_comm_rank;
  @   requires nxl > 0;      
  @   requires \mpi_valid(u, nxl + 2, MPI_DOUBLE);
  @   requires DISTRI;
  @   behavior hasLeft:
  @     assumes left != MPI_PROC_NULL;
  @     requires rank == \on(left, right);            // I'm the 'right' of my left
  @     assigns  \mpi_region(u, 1, MPI_DOUBLE);
  @     ensures  \on(left, u[nxl]) == u[0] && \on(left, u[nxl+1]) == u[1];
  @     waitsfor left;
  @   behavior hasRight:
  @     assumes right != MPI_PROC_NULL;
  @     requires rank == \on(right, left);            // I'm the 'left' of my right
  @     assigns  \mpi_region(&u[nxl+1], 1, MPI_DOUBLE);
  @     ensures  \on(right, u[1]) == u[nxl + 1] && \on(right, u[0]) == u[nxl];
  @     waitsfor right;
  @*/
void exchange_ghost_cells() {
  MPI_Sendrecv(&u[1], 1, MPI_DOUBLE, left, 0,
               &u[nxl+1], 1, MPI_DOUBLE, right, 0,
               MPI_COMM_WORLD, MPI_STATUS_IGNORE);
  MPI_Sendrecv(&u[nxl], 1, MPI_DOUBLE, right, 0,
               &u[0], 1, MPI_DOUBLE, left, 0,
               MPI_COMM_WORLD, MPI_STATUS_IGNORE);
}

/*@ requires \valid(u + (0 .. (nxl + 1)));
  @ requires \valid(u_new + (0 .. (nxl + 1)));
  @ requires k > 0 && nxl > 0;
  @ assigns  u[1 .. nxl], u_new[1 .. nxl];
  @ ensures  \forall int i; (0< i && i <= nxl)
  @           ==> 
  @          (u[i] == \old(u[i] + k*(u[i+1] + u[i-1] - 2*u[i])));
  @*/
void update() {
  /*@ loop invariant 1 <= i && i <= nxl + 1;
    @ loop invariant \forall int j; 1 <= j < i ==> 
    @        u_new[j] == u[j] + k*(u[j+1] + u[j-1] - 2*u[j]);
    @ //loop assigns u_new[0 .. nxl+1], i;
    @*/
  for (int i = 1; i <= nxl; i++)
    u_new[i] = u[i] + k*(u[i+1] + u[i-1] - 2*u[i]);
  double * tmp = u_new; u_new=u; u=tmp;
}

/*@ 
  @ \mpi_collective(MPI_COMM_WORLD, P2P):
  @   requires rank == \mpi_comm_rank;
  @   requires nprocs == \mpi_comm_size;
  @   requires 0 < nxl;
  @   requires DISTRI;
  @   requires k > 0.0 && \mpi_agree(k);
  @   requires \mpi_valid(u, nxl + 2, MPI_DOUBLE);
  @   requires \mpi_valid(u_new, nxl + 2, MPI_DOUBLE);
  @   assigns  u[1 .. nxl];
  @   ensures \forall int i; 2 <= i && i <= nxl-1 ==>
  @                          u[i] == \old(u[i] + k*(u[i+1] + u[i-1] - 2*u[i]));
  @   behavior hasLeftOnly:
  @     assumes left != MPI_PROC_NULL && right == MPI_PROC_NULL;
  @     requires rank == \on(left, right);
  @     assigns u[0];
  @     ensures u[0] == \old(\on(left, u[nxl]));
  @     ensures u[1] == \old(u[1] + k*(u[2] + \on(left, u[nxl]) - 2*u[1]));
  @     waitsfor left;
  @   behavior hasRightOnly:
  @     assumes right != MPI_PROC_NULL && left == MPI_PROC_NULL;
  @     requires rank == \on(right, left);
  @     assigns u[nxl+1];
  @     ensures u[nxl+1] == \old(\on(right, u[1]));
  @     ensures u[nxl] == \old(u[nxl] + k*(\on(right, u[1]) + u[nxl-1] - 2*u[nxl]));
  @     waitsfor right;
  @   behavior  hasBoth:
  @    assumes right != MPI_PROC_NULL && left != MPI_PROC_NULL;
  @    requires rank == \on(right, left) && rank == \on(left, right);
  @    assigns u[0], u[nxl+1];
  @    ensures u[nxl+1] == \old(\on(right, u[1])) && u[0] == \old(\on(left, u[nxl]));
  @    ensures nxl > 1 ==> u[1] == \old(u[1] + k*(u[2] + \on(left, u[nxl]) - 2*u[1]));
  @    ensures nxl > 1 ==> u[nxl] == \old(u[nxl] + k*(\on(right, u[1]) + u[nxl-1] - 2*u[nxl]));
  @    ensures nxl == 1 ==> u[1] == \old(u[1] + k*(\on(right, u[1]) + \on(left, u[nxl]) - 2*u[1]));
  @*/
void diff1d_iter() {
  exchange_ghost_cells();
  update();
}
