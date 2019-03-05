#include<mpi.h>
#include<civlc.cvh>
#include<stdio.h>

#pragma CIVL ACSL
#define DATA_LIMIT 1024

int left, right, nxl, nx, rank, nprocs;
double * u, * u_new, k;

#define OWNER(index) ((nprocs*(index+1)-1)/nx)

#define FIRST(rank) ((rank)*nx/nprocs)

#define LOCAL_OF(index) (index - (OWNER(index)*nx/nprocs) + 1)

#define READ(index)  (\on(OWNER(index), u)[LOCAL_OF(index)])

/* TODO: ideally the requirements for left and right neighbor shall be
 * that left != rank && right != rank && 
 *       the set L { l | \on(left, i), 0 <= i < \mpi_comm_size} equals
 *       to the set process rank set R {0, 1, ..., \mpi_comm_size-1, MPI_PROC_NULL}.
 *       ditto for 'right'.
 * 
 * But currently we can only assume that data is distributed in a typical way.
 */

/*@ \mpi_collective(MPI_COMM_WORLD, P2P):
  @   requires rank == \mpi_comm_rank;
  @   requires nxl > 0 && nxl < DATA_LIMIT;      
  @   requires \mpi_valid(u, nxl + 2, MPI_DOUBLE);
  @   requires left != \mpi_comm_rank && 
  @              ( 0 <= left && left < \mpi_comm_size || 
  @                left == MPI_PROC_NULL );
  @   requires right != \mpi_comm_rank && 
  @              ( 0 <= right && right < \mpi_comm_size || 
  @                right == MPI_PROC_NULL );
  @   requires right != left;
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
  @     ensures  \on(right, u[1]) == u[nxl + 1] && \on(right, u[1]) == u[nxl];        // intentianal error
  @     waitsfor right;
  @*/
void exchange_ghost_cells() {
  MPI_Sendrecv(&u[1], 1, MPI_DOUBLE, left, 0,
               &u[nxl+1], 1, MPI_DOUBLE, right, 0,
               MPI_COMM_WORLD, MPI_STATUS_IGNORE);
  // Incorrectly swap left and right
  MPI_Sendrecv(&u[nxl], 1, MPI_DOUBLE, left, 0,
               &u[0], 1, MPI_DOUBLE, right, 0,
               MPI_COMM_WORLD, MPI_STATUS_IGNORE);
  printf("rank=%d, left=%d, right=%d\n", rank, left, right);
}
