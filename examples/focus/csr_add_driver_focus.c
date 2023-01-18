#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <civlc.cvh>
#include <pointer.cvh>

#pragma CIVL ACSL

typedef struct
{
  int* i;
  int* j;
  int num_rows;
  int num_cols;
  int num_nonzeros;
  float* data;
} hypre_CSRMatrix;

$input int M;
$assume(1 <= M);
$input int N = 1;

$input float A[M][N], B[M][N];

int main(int argc, char* argv[])
{
  /* Initialize csr_A.i.
   */
  hypre_CSRMatrix csr_A;
  csr_A.num_rows = M;
  csr_A.num_cols = N;
  csr_A.i = (int*) malloc(sizeof(int) * (csr_A.num_rows + 1));
  /* Fill out the data of csr_A.i
   */
  csr_A.i[0] = 0;
  //@ focus R;
  /*@ loop invariant csr_A.i[0] == 0;
    @ loop invariant \forall int x; \forall int y; 0 <= x && x < y && y <= i ==> csr_A.i[x] <= csr_A.i[y] && csr_A.i[y] <= csr_A.i[x] + ((y-x)*N);
    @*/
  for (int i = 0; i < M; i++) {
    csr_A.i[i+1] = csr_A.i[i];
    for (int j = 0; j < N; j++) {
      if (A[i][j] != 0.0) {
        csr_A.i[i+1]++;
      }
    }
  }

  csr_A.num_nonzeros = csr_A.i[M];
  csr_A.data = NULL;
  csr_A.j = NULL;
  if (csr_A.num_nonzeros)
  {
    csr_A.data = (float*) malloc(sizeof(float) * csr_A.num_nonzeros);
    memset(csr_A.data, 0, sizeof(float) * csr_A.num_nonzeros);
    csr_A.j = (int*) malloc(sizeof(int) * csr_A.num_nonzeros);
    memset(csr_A.j, 0, sizeof(int) * csr_A.num_nonzeros);
  }


  /* Setup data of csr_A
   */
  int k = 0;
  //@ focus R;
  //@ loop invariant k == csr_A.i[i];
  for (int i = 0; i < M; ++i)
  {
    for (int j = 0; j < N; ++j)
    {
      if (A[i][j] != 0.0)
      {
        csr_A.data[k] = A[i][j];
        csr_A.j[k] = j;
        ++k;
      }
    }
  }

  
  /* Initialize csr_B
   */
  hypre_CSRMatrix csr_B;
  csr_B.num_rows = M;
  csr_B.num_cols = N;
  csr_B.i = (int*) malloc(sizeof(int) * (csr_B.num_rows + 1));
  /* Fill out the data of csr_B.i
   */
  csr_B.i[0] = 0;
  //@ focus R;
  /*@ loop invariant csr_B.i[0] == 0;
    @ loop invariant \forall int x; \forall int y; 0 <= x && x < y && y <= i ==> csr_B.i[x] <= csr_B.i[y] && csr_B.i[y] <= csr_B.i[x] + ((y-x)*N);
    @*/
  for (int i = 0; i < M; i++) {
    csr_B.i[i+1] = csr_B.i[i];
    for (int j = 0; j < N; j++) {
      if (B[i][j] != 0.0) {
        csr_B.i[i+1]++;
      }
    }
  }
  
  csr_B.num_nonzeros = csr_B.i[M];
  csr_B.data = NULL;
  csr_B.j = NULL;
  if (csr_B.num_nonzeros)
  {
    csr_B.data = (float*) malloc(sizeof(float) * csr_B.num_nonzeros);
    memset(csr_B.data, 0, sizeof(float) * csr_B.num_nonzeros);
    csr_B.j = (int*) malloc(sizeof(int) * csr_B.num_nonzeros);
    memset(csr_B.j, 0, sizeof(int) * csr_B.num_nonzeros);
  }


  /* Setup data of csr_B.
   */
  k = 0;
  //@ focus R;
  /*@ loop invariant k == csr_B.i[i];
    @*/
  for (int i = 0; i < M; ++i)
  {
    for (int j = 0; j < N; ++j)
    {
      if (B[i][j] != 0.0)
      {
        csr_B.data[k] = B[i][j];
        csr_B.j[k] = j;
        ++k;
      }
    }
  }

  
  /** Alg start **/
  hypre_CSRMatrix csr_C;
  int* marker;

  marker = (int*) malloc(sizeof(int) * N);
  memset(marker, 0, sizeof(int) * N);
  
  csr_C.i = (int*) malloc(sizeof(int) * (M+1));
  memset(csr_C.i, 0, sizeof(int) * (M+1));

  /* Initialize elems of marker to -1.
   */
  for (int ia = 0; ia < N; ia++)
  {
    marker[ia] = -1;
  }
  

  /* Count number of nonzeros of C
   */
  int num_nonzeros = 0;
  csr_C.i[0] = 0;
  //@ focus R;
  /*@ loop invariant num_nonzeros == csr_C.i[ic];
    @ loop invariant csr_C.i[0] == 0;
    @ loop invariant \forall int x; \forall int y; 0 <= x && x < y && y <= ic ==>
    @                  csr_C.i[x] <= csr_C.i[y] &&
    @                  csr_C.i[y] <= csr_C.i[x] + ((y-x)*N);
    @ loop invariant \forall int t; 0 <= t && t < N ==> marker[t] < ic;
    @*/
  for (int ic = 0; ic < M; ic++)
  {
    for (int ia = csr_A.i[ic]; ia < csr_A.i[ic+1]; ia++)
    {
      int jcol = csr_A.j[ia];
      marker[jcol] = ic;
      num_nonzeros++;
    }
    for (int ib = csr_B.i[ic]; ib < csr_B.i[ic+1]; ib++)
    {
      int jcol = csr_B.j[ib];
      if (marker[jcol] != ic)
      {
        marker[jcol] = ic;
        num_nonzeros++;
      }
    }
    csr_C.i[ic+1] = num_nonzeros;
  }

  
  /* Initialize csr_C.
   */
  csr_C.num_rows = M;
  csr_C.num_cols = N;
  csr_C.num_nonzeros = num_nonzeros;
  csr_C.data = NULL;
  csr_C.j = NULL;
  if (csr_C.num_nonzeros)
  {
    csr_C.data = (float*) malloc(sizeof(float) * csr_C.num_nonzeros);
    memset(csr_C.data, 0, sizeof(float) * csr_C.num_nonzeros);
    csr_C.j = (int*) malloc(sizeof(int) * csr_C.num_nonzeros);
    memset(csr_C.j, 0, sizeof(int) * csr_C.num_nonzeros);
  }


  /* Initialize elements of marker to -1.
   */
  for (int ia = 0; ia < N; ia++)
  {
    marker[ia] = -1;
  }

  
  int pos = 0;
  //@ focus R;
  /*@ loop invariant pos == csr_C.i[ic];
    @ loop invariant \forall int t; 0 <= t && t < N ==> marker[t] < pos;
    @*/
  for (int ic = 0; ic < M; ic++)
  {
    for (int ia = csr_A.i[ic]; ia < csr_A.i[ic+1]; ia++)
    {
      int jcol = csr_A.j[ia];
      csr_C.j[pos] = jcol;
      csr_C.data[pos] = csr_A.data[ia];
      marker[jcol] = pos;
      pos++;
    }
    for (int ib = csr_B.i[ic]; ib < csr_B.i[ic+1]; ib++)
    {
      int jcol = csr_B.j[ib];
      if (marker[jcol] < csr_C.i[ic])
      {
        csr_C.j[pos] = jcol;
        csr_C.data[pos] = csr_B.data[ib];
        marker[jcol] = pos;
        pos++;
      }
      else
      {
        csr_C.data[marker[jcol]] += csr_B.data[ib];
      }
    }
  }
  free(marker);
  marker = NULL;
  /** Alg end **/

  
  /* Initialize elements of impl_C to 0.
   */
  float impl_C[M][N];
  //@ focus R;
  for (int i = 0; i < M; ++i)
  {
    for (int j = 0; j < N; ++j)
    {
      impl_C[i][j] = 0.0;
    }
  }
  

  /* Merge in nonzero elements of csr_C into impl_C.
   */
  //@ focus R;
  for (int i = 0; i < M; ++i)
  {
    int start = csr_C.i[i], end = csr_C.i[i+1];
    for (int k = start; k < end; ++k)
    {
      impl_C[i][csr_C.j[k]] = csr_C.data[k];
    }
  }

  /* Set up spec_C to be A + B.
   */
  float spec_C[M][N];
  //@ focus R;
  for (int i = 0; i < M; ++i)
  {
    for (int j = 0; j < N; ++j)
    {
      spec_C[i][j] = A[i][j] + B[i][j];
    }
  }

  //@ focus R;
  $assert($forall(int i:0..M-1;int j:0..N-1) spec_C[i][j] == impl_C[i][j]);
}
