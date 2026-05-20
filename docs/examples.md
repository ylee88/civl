## Sparse Matrix-Vector Multiplication

In this example, we verify the functional correctness of a matrix-vector multiplication
routine where the matrix is represented in a sparse (Compressed Sparse Row)
representation.

```c title="csr.h"
#ifndef _CSR_H
#define _CSR_H
/* Compressed Sparse Row (CSR) sparse matrix representation. */

struct csr_matrix {
  double *val; // length NZ (number of non-0s)
  int *col_ind; // length NZ
  int *row_ptr; // length N+1.  row_ptr[N]=NZ.
  int rows, cols; // number of rows (N), number of columns
};

/* Multiplies matrix m and (dense) vector v, storing result in already
   allocated vector p.  If m has dimensions NxM then v has length M
   and p has length N. */
void csr_matrix_vector_multiply(struct csr_matrix *m, double *v, double *p);

#endif
```

```c title="csr.c"
/* Implementation of CSR matrix-vector multiplication from "LAProof: A
   Library of Formal Proofs of Accuracy and Correctness for Linear
   Algebra Programs" by Kellison, Appel, Tekriwal, and Bindel, IEEE
   30th Symposium on Computer Arithmetic, 2023.  That paper cites
   Barrett et al., "Templates for the Solution of Linear Systems:
   Building Blocks for Iterative Methods", SIAM, 1994, as the source
   of the algorithm. */
#include "csr.h"

#define fma(x,y,s) (x*y+s)

void csr_matrix_vector_multiply(struct csr_matrix *m, double *v, double *p) {
  int i, rows=m->rows;
  double *val = m->val;
  int *col_ind = m->col_ind;
  int *row_ptr = m->row_ptr;
  int next=row_ptr[0];
  for (i=0; i<rows; i++) {
    double s=0.0;
    int h=next;
    next=row_ptr[i+1];
    for (; h<next; h++) {
      double x = val[h];
      int j = col_ind[h];
      double y = v[j];
      s = fma(x,y,s);
    }
    p[i]=s;
  }
}
```

```civl title="csr_driver.cvl"
/* Filename : csr_driver.cvl
   Author   : Stephen F. Siegel
   Created  : 2023-08-03
   Modified : 2023-08-07

   Verification of matrix-vector multiplication routine using
   Compressed Sparse Row representation for the matrix.  Start with an
   arbitrary CSR matrix with N and M under specified bounds.  Take an
   arbitrary vector v of length M.  Call sparse matrix-vector
   multiplication routine to compute "actual" resulting vector.
   Convert the sparse matrix to a dense matrix and compute dense
   matrix-vector product to get "expected" result.  Check the actual
   and expected agree.
*/
#include <stdio.h>
#include <stdlib.h>
#include <pointer.cvh>
#include "csr.h"

typedef struct $mat {
  int n, m; // num rows, columns;
  double data[][];
} $mat;

void $mat_vec_mul($mat mat, double * v, double *p) {
  int n = mat.n, m = mat.m;
  for (int i=0; i<n; i++) {
    double s = 0.0;
    for (int j=0; j<m; j++) s += mat.data[i][j]*v[j];
    p[i] = s;
  }
}

$mat $mat_csr(struct csr_matrix csr) {
  int n = csr.rows, m = csr.cols;
  $mat mat;
  mat.n = n;
  mat.m = m;
  mat.data = (double[n][m])$lambda(int i,j) 0.0;
  for (int i=0; i<n; i++) {
    int r = csr.row_ptr[i], rnxt = csr.row_ptr[i+1];
    for (int k=r; k<rnxt; k++)
      mat.data[i][csr.col_ind[k]] = csr.val[k];
  }
  return mat;
}

// N,M=number of rows,columns.  N_B,M_B=upper bounds on N,M
$input int N_B = 3, M_B = 3, N, M;
$assume (1<=N && N<=N_B && 1<=M && M<=M_B);
// the non-0 entries for the matrix and the (dense) vector
$input double A[N*M], V[M];

/* Fills in p[0],...,p[len-1] with a strictly increasing sequence
   of integers in [0,max].  Precondition: 0 <= len <= max+1 */
void strict_inc(int * p, int len, int max) {
  //$assert(0<=len && len <= max+1);
  for (int i=0; i<len; i++) {
    int a = (i == 0 ? 0 : p[i-1]+1), b = max - len + i + 1;
    p[i] = a + $choose_int(b-a+1); // choose in a..b
  }
}

/* Nondeterministically create a CSR matrix with n rows and
   m columns */
struct csr_matrix make_csr(int n, int m) {
  int * row_ptr = malloc((n+1)*sizeof(int));
  row_ptr[0] = 0;
  for (int i=1; i<=n; i++)
    row_ptr[i] = row_ptr[i-1]+$choose_int(m+1);
  int NZ = row_ptr[n];
  $assert(0<=NZ && NZ<=n*m);
  int * col_ind = malloc(NZ*sizeof(int));
  for (int i=0; i<n; i++)
    strict_inc(col_ind+row_ptr[i], row_ptr[i+1]-row_ptr[i], m-1);
  double * val = malloc(NZ*sizeof(double));
  for (int i=0; i<NZ; i++) val[i] = A[i];
  return (struct csr_matrix){ val, col_ind, row_ptr, n, m };
}

void print_int_array(int * p, int n) {
  printf("{ ");
  for (int i=0; i<n; i++) printf("%d ", p[i]);
  printf("}");
}

void print_dbl_array(double * p, int n) {
  printf("{ ");
  for (int i=0; i<n; i++) printf("%lf ", p[i]);
  printf("}");
}

void print_csr(struct csr_matrix mat) {
  int nz = mat.row_ptr[mat.rows];
  printf("begin CSR[nrow=%d, ncol=%d, nz=%d]",
         mat.rows, mat.cols, nz);
  printf("\n  row_ptr = ");
  print_int_array(mat.row_ptr, mat.rows+1);
  printf("\n  col_ind = ");
  print_int_array(mat.col_ind, nz);
  printf("\n  val     = ");
  print_dbl_array(mat.val, nz);
  printf("\nend CSR\n");
}

void destroy_csr(struct csr_matrix mat) {
  free(mat.row_ptr);
  free(mat.col_ind);
  free(mat.val);
}

int main(void) {
  double v[M], actual[N], expected[N];
  for (int i=0; i<M; i++) v[i] = V[i];
  struct csr_matrix mat = make_csr(N, M);
  csr_matrix_vector_multiply(&mat, v, actual);
  $mat dense = $mat_csr(mat);
  $mat_vec_mul(dense, v, expected);
#ifdef VERBOSE
  printf("\n");
  print_csr(mat);
  printf("v         = ");
  print_dbl_array(v, M);
  printf("\nactual    = ");
  print_dbl_array(actual, N);
  printf("\nexpected  = ");
  print_dbl_array(expected, N);
  printf("\n");
#endif
  destroy_csr(mat);
  $assert($equals(actual, expected));
}
```

```make title="Makefile"
small: csr.h csr.c csr_driver.cvl
	civl verify -DVERBOSE csr.c csr_driver.cvl

medium: csr.h csr.c csr_driver.cvl
	civl verify -inputN_B=3 -inputM_B=4 csr.c csr_driver.cvl

medium2: csr.h csr.c csr_driver.cvl
	civl verify -inputN_B=4 -inputM_B=3 csr.c csr_driver.cvl

large: csr.h csr.c csr_driver.cvl
	civl verify -inputN_B=4 -inputM_B=4 csr.c csr_driver.cvl

clean:
	rm -rf CIVLREP *~ *.out *.tmp
```

Output from `make` (excerpted):
```
civl verify -DVERBOSE csr.c csr_driver.cvl
CIVL v1.22+ of 2023-10-09 -- http://vsl.cis.udel.edu/civl

begin CSR[nrow=3, ncol=3, nz=0]
  row_ptr = { 0 0 0 0 }
  col_ind = { }
  val     = { }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 0 }
expected  = { 0 0 0 }

begin CSR[nrow=3, ncol=3, nz=1]
  row_ptr = { 0 0 0 1 }
  col_ind = { 0 }
  val     = { X_A[0] }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 X_V[0]*X_A[0] }
expected  = { 0 0 X_V[0]*X_A[0] }

begin CSR[nrow=3, ncol=3, nz=1]
  row_ptr = { 0 0 0 1 }
  col_ind = { 1 }
  val     = { X_A[0] }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 X_V[1]*X_A[0] }
expected  = { 0 0 X_V[1]*X_A[0] }

begin CSR[nrow=3, ncol=3, nz=1]
  row_ptr = { 0 0 0 1 }
  col_ind = { 2 }
  val     = { X_A[0] }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 X_V[2]*X_A[0] }
expected  = { 0 0 X_V[2]*X_A[0] }

begin CSR[nrow=3, ncol=3, nz=2]
  row_ptr = { 0 0 0 2 }
  col_ind = { 0 1 }
  val     = { X_A[0] X_A[1] }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 (X_V[1]*X_A[1])+(X_V[0]*X_A[0]) }
expected  = { 0 0 (X_V[1]*X_A[1])+(X_V[0]*X_A[0]) }

begin CSR[nrow=3, ncol=3, nz=2]
  row_ptr = { 0 0 0 2 }
  col_ind = { 0 2 }
  val     = { X_A[0] X_A[1] }
end CSR
v         = { X_V[0] X_V[1] X_V[2] }
actual    = { 0 0 (X_V[2]*X_A[1])+(X_V[0]*X_A[0]) }
expected  = { 0 0 (X_V[2]*X_A[1])+(X_V[0]*X_A[0]) }
.
.
.
begin CSR[nrow=3, ncol=2, nz=6]
  row_ptr = { 0 2 4 6 }
  col_ind = { 0 1 0 1 0 1 }
  val     = { X_A[0] X_A[1] X_A[2] X_A[3] X_A[4] X_A[5] }
end CSR
v         = { X_V[0] X_V[1] }
actual    = { (X_V[1]*X_A[1])+(X_V[0]*X_A[0]) (X_V[1]*X_A[3])+(X_V[0]*X_A[2]) (X_V[1]*X_A[5])+(X_V[0]*X_A[4]) }
expected  = { (X_V[1]*X_A[1])+(X_V[0]*X_A[0]) (X_V[1]*X_A[3])+(X_V[0]*X_A[2]) (X_V[1]*X_A[5])+(X_V[0]*X_A[4]) }
.
.
.
begin CSR[nrow=1, ncol=1, nz=1]
  row_ptr = { 0 1 }
  col_ind = { 0 }
  val     = { X_A[0] }
end CSR
v         = { X_V[0] }
actual    = { X_A[0]*X_V[0] }
expected  = { X_A[0]*X_V[0] }

=== Source files ===
csr.c  (csr.c)
csr.h  (csr.h)
csr_driver.cvl  (csr_driver.cvl)

=== Command ===
civl verify -DVERBOSE csr.c csr_driver.cvl

=== Stats ===
   time (s)          : 9.22          transitions  : 215091
   memory (bytes)    : 6.62700032E8  trace steps  : 175869
   max process count : 1             valid calls  : 661988
   states            : 175197        provers      : cvc4, z3
   states saved      : 246990        prover calls : 19
   state matches     : 673

=== Result ===
All errors marked with '+' are absent on all executions.
 + Dereference errors                  + Functional equivalence violations
 + Internal errors                     + Library loading errors
 + Other errors                        + Assertion violations
 + Communication errors                + Writes to constant variables
 + Absolute deadlocks                  + Division by zero
 + Writes to $input variables          + Invalid casts
 + Malloc errors                       + Memory leaks
 + Memory management errors            + MPI usage errors
 + Out of bounds errors                + Reads from $output variables
 + Pointer errors                      + Process leaks
 + Sequence errors                     - Non-termination
 + Use of undefined values             + Union errors
```

Times on a 2020 M1 MacBook Pro:

| Matrices  | Time  |
|-----------|-------|
| Up to 3x3 | 6s    |
| Up to 3x4 | 17s   |
| Up to 4x3 | 17s   |
| Up to 4x4 | 19min |
