#include <civlc.cvh>
#include <pointer.cvh>

#include "HYPRE_utilities.h"
#include "csr_matrix.h"
#include "hypre_memory.h"

$input int BOUND;

$input int M;
$assume(1 <= M);
//$assume(1 <= M && M <= BOUND);

$input int N;
$assume(1 <= N && N <= BOUND);
$assume(1 <= N);

$input HYPRE_Real A[M][N], B[M][N];

/** Original **/

/* hypre_CSRMatrix* */
/* hypre_CSRMatrixAddHost ( hypre_CSRMatrix *A, */
/*                          hypre_CSRMatrix *B ) */
/* { */
/*    HYPRE_Complex    *A_data   = hypre_CSRMatrixData(A); */
/*    HYPRE_Int        *A_i      = hypre_CSRMatrixI(A); */
/*    HYPRE_Int        *A_j      = hypre_CSRMatrixJ(A); */
/*    HYPRE_Int         nrows_A  = hypre_CSRMatrixNumRows(A); */
/*    HYPRE_Int         ncols_A  = hypre_CSRMatrixNumCols(A); */
/*    HYPRE_Complex    *B_data   = hypre_CSRMatrixData(B); */
/*    HYPRE_Int        *B_i      = hypre_CSRMatrixI(B); */
/*    HYPRE_Int        *B_j      = hypre_CSRMatrixJ(B); */
/*    HYPRE_Int         nrows_B  = hypre_CSRMatrixNumRows(B); */
/*    HYPRE_Int         ncols_B  = hypre_CSRMatrixNumCols(B); */
/*    hypre_CSRMatrix  *C; */
/*    HYPRE_Complex    *C_data; */
/*    HYPRE_Int        *C_i; */
/*    HYPRE_Int        *C_j; */

/*    HYPRE_Int         ia, ib, ic, jcol, num_nonzeros; */
/*    HYPRE_Int         pos; */
/*    HYPRE_Int         *marker; */

/*    HYPRE_MemoryLocation memory_location_A = hypre_CSRMatrixMemoryLocation(A); */
/*    HYPRE_MemoryLocation memory_location_B = hypre_CSRMatrixMemoryLocation(B); */

/*    /\* RL: TODO cannot guarantee, maybe should never assert */
/*    hypre_assert(memory_location_A == memory_location_B); */
/*    *\/ */

/*    /\* RL: in the case of A=H, B=D, or A=D, B=H, let C = D, */
/*     * not sure if this is the right thing to do. */
/*     * Also, need something like this in other places */
/*     * TODO *\/ */
/*    HYPRE_MemoryLocation memory_location_C = hypre_max(memory_location_A, memory_location_B); */

/*    if (nrows_A != nrows_B || ncols_A != ncols_B) */
/*    { */
/*       hypre_error_w_msg(HYPRE_ERROR_GENERIC,"Warning! incompatible matrix dimensions!\n"); */
/*       return NULL; */
/*    } */

/*    marker = hypre_CTAlloc(HYPRE_Int, ncols_A, HYPRE_MEMORY_HOST); */
/*    C_i = hypre_CTAlloc(HYPRE_Int, nrows_A+1, memory_location_C); */

/*    for (ia = 0; ia < ncols_A; ia++) */
/*    { */
/*       marker[ia] = -1; */
/*    } */

/*    num_nonzeros = 0; */
/*    C_i[0] = 0; */
/*    for (ic = 0; ic < nrows_A; ic++) */
/*    { */
/*       for (ia = A_i[ic]; ia < A_i[ic+1]; ia++) */
/*       { */
/*          jcol = A_j[ia]; */
/*          marker[jcol] = ic; */
/*          num_nonzeros++; */
/*       } */
/*       for (ib = B_i[ic]; ib < B_i[ic+1]; ib++) */
/*       { */
/*          jcol = B_j[ib]; */
/*          if (marker[jcol] != ic) */
/*          { */
/*             marker[jcol] = ic; */
/*             num_nonzeros++; */
/*          } */
/*       } */
/*       C_i[ic+1] = num_nonzeros; */
/*    } */

/*    C = hypre_CSRMatrixCreate(nrows_A, ncols_A, num_nonzeros); */
/*    hypre_CSRMatrixI(C) = C_i; */
/*    hypre_CSRMatrixInitialize_v2(C, 0, memory_location_C); */
/*    C_j = hypre_CSRMatrixJ(C); */
/*    C_data = hypre_CSRMatrixData(C); */

/*    for (ia = 0; ia < ncols_A; ia++) */
/*    { */
/*       marker[ia] = -1; */
/*    } */
/*    pos = 0; */
/*    for (ic = 0; ic < nrows_A; ic++) */
/*    { */
/*       for (ia = A_i[ic]; ia < A_i[ic+1]; ia++) */
/*       { */
/*          jcol = A_j[ia]; */
/*          C_j[pos] = jcol; */
/*          C_data[pos] = A_data[ia]; */
/*          marker[jcol] = pos; */
/*          pos++; */
/*       } */
/*       for (ib = B_i[ic]; ib < B_i[ic+1]; ib++) */
/*       { */
/*          jcol = B_j[ib]; */
/*          if (marker[jcol] < C_i[ic]) */
/*          { */
/*             C_j[pos] = jcol; */
/*             C_data[pos] = B_data[ib]; */
/*             marker[jcol] = pos; */
/*             pos++; */
/*          } */
/*          else */
/*          { */
/*             C_data[marker[jcol]] += B_data[ib]; */
/*          } */
/*       } */
/*    } */

/*    hypre_TFree(marker, HYPRE_MEMORY_HOST); */

/*    return C; */
/* } */

hypre_CSRMatrix*
hypre_CSRMatrixAddHost ( hypre_CSRMatrix *A,
                         hypre_CSRMatrix *B )
{
   HYPRE_Complex    *A_data   = hypre_CSRMatrixData(A);
   HYPRE_Int        *A_i      = hypre_CSRMatrixI(A);
   HYPRE_Int        *A_j      = hypre_CSRMatrixJ(A);
   HYPRE_Int         nrows_A  = hypre_CSRMatrixNumRows(A);
   HYPRE_Int         ncols_A  = hypre_CSRMatrixNumCols(A);
   HYPRE_Complex    *B_data   = hypre_CSRMatrixData(B);
   HYPRE_Int        *B_i      = hypre_CSRMatrixI(B);
   HYPRE_Int        *B_j      = hypre_CSRMatrixJ(B);
   HYPRE_Int         nrows_B  = hypre_CSRMatrixNumRows(B);
   HYPRE_Int         ncols_B  = hypre_CSRMatrixNumCols(B);
   hypre_CSRMatrix  *C;
   HYPRE_Complex    *C_data;
   HYPRE_Int        *C_i;
   HYPRE_Int        *C_j;

   HYPRE_Int         ia, ib, ic, jcol, num_nonzeros;
   HYPRE_Int         pos;
   HYPRE_Int         *marker;

   hypre_CTAlloc(HYPRE_Int, marker, ncols_A);
   hypre_CTAlloc(HYPRE_Int, C_i, nrows_A+1);

   for (ia = 0; ia < ncols_A; ia++)
   {
      marker[ia] = -1;
   }

   num_nonzeros = 0;
   C_i[0] = 0;
   //@ transform flatten tag1;
   for (ic = 0; ic < nrows_A; ic++)
   {
      for (ia = A_i[ic]; ia < A_i[ic+1]; ia++)
      {
         jcol = A_j[ia];
         marker[jcol] = ic;
         num_nonzeros++;
      }
      for (ib = B_i[ic]; ib < B_i[ic+1]; ib++)
      {
         jcol = B_j[ib];
         if (marker[jcol] != ic)
         {
            marker[jcol] = ic;
            num_nonzeros++;
         }
      }
      C_i[ic+1] = num_nonzeros;
   }

   C = hypre_CSRMatrixCreate(nrows_A, ncols_A, num_nonzeros);
   hypre_CSRMatrixI(C) = C_i;
   hypre_CSRMatrixInitialize_v2(C);
   C_j = hypre_CSRMatrixJ(C);
   C_data = hypre_CSRMatrixData(C);

   for (ia = 0; ia < ncols_A; ia++)
   {
      marker[ia] = -1;
   }
   pos = 0;
   for (ic = 0; ic < nrows_A; ic++)
   {
      for (ia = A_i[ic]; ia < A_i[ic+1]; ia++)
      {
         jcol = A_j[ia];
         C_j[pos] = jcol;
         C_data[pos] = A_data[ia];
         marker[jcol] = pos;
         pos++;
      }
      for (ib = B_i[ic]; ib < B_i[ic+1]; ib++)
      {
         jcol = B_j[ib];
         if (marker[jcol] < C_i[ic])
         {
            C_j[pos] = jcol;
            C_data[pos] = B_data[ib];
            marker[jcol] = pos;
            pos++;
         }
         else
         {
            C_data[marker[jcol]] += B_data[ib];
         }
      }
   }

   hypre_TFree(marker);

   return C;
}

hypre_CSRMatrix *
DenseToCSR (int m, int n, HYPRE_Real a[][])
{
  int numnonzeros = 0;
  for (int i = 0; i < m; ++i)
  {
    for (int j = 0; j < n; ++j)
    {
      if (a[i][j] != 0.0)
      {
        ++numnonzeros;
      }
    }
  }

  hypre_CSRMatrix * csr_a = hypre_CSRMatrixCreate(m, n, numnonzeros);
  hypre_CSRMatrixInitialize_v2(csr_a);

  int k = 0;
  
  for (int i = 0; i < m; ++i)
  {
    hypre_CSRMatrixI(csr_a)[i] = k;
    for (int j = 0; j < n; ++j)
    {
      if (a[i][j] != 0.0)
      {
        hypre_CSRMatrixData(csr_a)[k] = a[i][j];
        hypre_CSRMatrixJ(csr_a)[k] = j;
        ++k;
      }
    }
  }
  hypre_CSRMatrixI(csr_a)[m] = numnonzeros;

  return csr_a;
}

void
CSRToDense(HYPRE_Real dense[][], hypre_CSRMatrix * csr)
{
  int m = hypre_CSRMatrixNumRows(csr), n = hypre_CSRMatrixNumCols(csr);

  for (int i = 0; i < m; ++i)
  {
    for (int j = 0; j < n; ++j)
    {
      dense[i][j] = 0.0;
    }
  }

  for (int i = 0; i < m; ++i)
  {
    int start = hypre_CSRMatrixI(csr)[i], end = hypre_CSRMatrixI(csr)[i+1];
    for (int k = start; k < end; ++k)
    {
      dense[i][hypre_CSRMatrixJ(csr)[k]] = hypre_CSRMatrixData(csr)[k];
    }
  }
}

void
AddSpec(int m, int n, HYPRE_Real c[][], HYPRE_Real a[][], HYPRE_Real b[][])
{
  for (int i = 0; i < m; ++i)
  {
    for (int j = 0; j < n; ++j)
    {
      c[i][j] = a[i][j] + b[i][j];
    }
  }
}

void free_CSR(hypre_CSRMatrix * matrix) {
  free(matrix->data);
  free(matrix->i);
  free(matrix->j);
  free(matrix);
}

int main(int argc, char* argv[])
{
  $atomic {
    $elaborate(M);
    $elaborate(N);
    HYPRE_Real C[M][N];
    AddSpec(M, N, C, A, B);

    hypre_CSRMatrix * csr_A = DenseToCSR(M, N, A);
    hypre_CSRMatrix * csr_B = DenseToCSR(M, N, B);

    HYPRE_Real impl_A[M][N];
    CSRToDense(impl_A, csr_A);
    //$assert($equals(&A, &impl_A));

    hypre_CSRMatrix * csr_C = hypre_CSRMatrixAddHost(csr_A, csr_B);

    HYPRE_Real impl_C[M][N];
    CSRToDense(impl_C, csr_C);
  
    $assert($equals(&C, &impl_C));

    free_CSR (csr_A), free_CSR (csr_B), free_CSR (csr_C);
  }
}
