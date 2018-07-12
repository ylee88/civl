extern void __VERIFIER_error() __attribute__ ((__noreturn__));
extern void __VERIFIER_assume(int);
extern int __VERIFIER_nondet_int();

/* This program tests that CIVL can process code like that in
 * SVCOMP examples with explicit branch direction. This will
 * be excercising the Directing Transformer as well as code
 * in CIVL.Slice. A run of 

    `verify -svcomp16 stress_test.c`

 * followed by

    `replay -sliceAnalysis -direct=stress_test.direct stress_test.c`

 * is expected to output an interface (defined in stress_test.oracle) 
 * to another analysis engine that can consume a sliced path
 * condition with a mapping from the symbolic variables to
 * their syntactic concrete aliases.
 */

int main () {
  int x = __VERIFIER_nondet_int();
  int y = __VERIFIER_nondet_int();
  int z = 0; /* Will receive symbolic read in loop */
  int z_0 = 0; /* Will receive symbolic read in loop */
  int irrelevant = 0;

  /* Test that different input reads in 
     a loop are correctly transformed */
  for (int i = 0; i < 2; i++) {
    z = __VERIFIER_nondet_int();
    if (i == 0) {
      z_0 = z;
    }
  }

  /* Test that this branch which is independent
     of hitting the error, gets sliced away */
  if (y > 0) {
    irrelevant = 42;
  }

  /* Test that this branch, which contains no
     symbolic constraints, is not counted as
     being sliced away */
  if (irrelevant > 0) {
    irrelevant = 42;
  }

  if (z < 0) {
    if (z_0 < 0) {
      if (x < 0) {
        /* Test " while (1)" loops work with direction */
        while (1) {
          __VERIFIER_error();
	}
      }
    }
  }
    
}
