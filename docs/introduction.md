
# Introduction and Quick Start

## Installation and Quick Start

1. Install one or more of the following automated theorem provers.
   In each case, you must ensure that the executable
   (z3, cvc4, cvc5, alt-ergo) is in your PATH.
    - [Z3](https://github.com/Z3Prover/z3)
    - [CVC4](https://cvc4.github.io)
    - [cvc5](https://github.com/cvc5/cvc5)
    - [Alt-Ergo](https://alt-ergo.ocamlpro.com)
1. Install a Java SDK if you have not already done so.  A package
   manager is the easiest way to do this.  We recommend the latest
   long term support release, but CIVL should work fine with Java 17
   or later.
1. The CIVL releases are published on [the GitHub releases
   page](<https://github.com/verified-software-lab/civl/releases/>).
   Choose your desired version; for most users, the latest stable
   version is the best choice.  In any case, this will be a tar
   gzipped archive with a name of the form `civl-x.y.tgz`.  Unpack
   this and you should have a directory named `civl-x.y`.  The
   directory contains jar files, source code, examples, and other
   resources.  You can move it wherever you like.
1. Use one of the following methods to create the `civl` executable:
    - Method 1 (jlink): Change into the `civl-x.y` directory and type
      `make`.  This should result in a directory named `civl-runtime`
      which contains a custom JVM optimized for CIVL.  You can move
      `civl-runtime` wherever you like (or keep it where it is).  Add
      the `/path/to/civl-runtime/bin` to your PATH, e.g., by adding a
      line such as `export PATH=/path/to/civl-runtime/bin:$PATH` to
      your shell startup file (`.zprofile`, `.bash_profile`, etc.).
      See the comments in the `Makefile` for more details and options.
    - Method 2 (jar): Move the file in `civl-x.y/lib` named
      `civl-complete.jar` to wherever you like (or keep it where it
      is).  Create an executable shell script like the following:
      ```
      #!/bin/sh
      java -Xmx16g -jar /path/to/civl-complete.jar $@
      ```
      Adjust the JVM arguments however you like; in the example above,
	  the maximum heap size is set to 16GB.  Move this script into a
      directory in your PATH.
1. From the command line, type `civl help`. You should see a help
   message describing the command line syntax.
1. From the command line, type `civl config`. This should find the
   provers in your PATH and create a file named `.sarl` in your home
   directory.

To test your installation, copy the file
`examples/concurrency/locksBad.cvl` to your working directory. Look at
the program: it is a simple 2-process program with two shared
variables used as locks. The two processes try to obtain the locks in
opposite order, which can lead to a deadlock if both processes obtain
their first lock before either obtains the second. Type `civl verify
locksBad.cvl`. You should see some output culminating in a message

`The program MAY NOT be correct.  See CIVLREP/locksBad_log.txt.`

Type `civl replay locksBad.cvl`. You should see a step-by-step account
of how the program arrived at the deadlock.


## Verifying CIVL-C Programs

Dijkstra’s well-known Dining Philosophers system can be encoded in
CIVL-C as follows:

```civl
$input int B = 4; // upper bound on number of philosophers
$input int n;     // number of philosophers
$assume(2<=n && n<=B);

_Bool forks[n]; // Each fork will be on the table ($true) or in a hand ($false).

void dine(int id) {
  int left = id;
  int right = (id + 1) % n;
  while (1) {
    $when (forks[left]) forks[left] = $false;
    $when (forks[right]) forks[right] = $false;
    forks[right] = $true;
    forks[left] = $true;
  }
}

void main() {
  $for(int i: 0..n-1) forks[i] = $true;
  $parfor(int i: 0..n-1) dine(i);
}
```

In this encoding, an upper bound `B` is placed on the number of
philosophers `n`. When verifying this program, a concrete value will
be specified for `B`. Hence the result of verification will apply to
all `n` between 2 and `B`, inclusive.

Both `B` and `n` are declared as input variables using the type
qualifier `$input`. An input variable may be initialized with any
valid value of its type. In contrast, non-input variables declared in
file scope will be initialized with a special undefined value; if such
a variable is read before it is defined, an error will be reported. In
addition, any input variable may have a concrete initial value
specified on the command line. In this case, we will specify a
concrete value for `B` on the command line.

An `$assume` statement restricts the set of executions of the program
to include only those traces in which the assumptions hold. In
contrast with an `$assert` statement, CIVL does not check that the
assumed expression holds, and will not generate an error message if it
fails to hold. Thus an `$assume` statement allows the programmer to
say to CIVL “assume that this is true,” while an `$assert` statement
allows the programmer to say to CIVL “check that this is true.”

A `$when` statement encodes a guarded command. The `$when` statement
includes a boolean expression called the **guard** and a statement
body. The `$when` statement is enabled if and only if the guard
evaluates to **true**, in which case the body may be executed. The
first atomic statement in the body executes atomically with the
evaluation of the guard, so it is guaranteed that the guard will hold
when this initial sub-statement executes. Since assignment statements
are atomic in CIVL, in this example the body of each `$when` statement
executes atomically with the guard evaluation.

The `$for` statement is very similar to a **for** loop. The main
difference is that it takes a **domain** and loops over it.

The `$parfor` statement is a combination of `$for` and `$spawn`. The
latter is very similar to a function call. The main difference is that
the function called is invoked in a new process which runs
concurrently with the existing processes.

The program may be verified for an upper bound of 5 by typing

```sh
civl verify -inputB=5 diningBad.cvl
```

which results in the following output:

```
CIVL v1.18+ of 2018-12-28 -- http://vsl.cis.udel.edu/civl

Violation 0 encountered at depth 21:
CIVL execution violation (kind: DEADLOCK, certainty: PROVEABLE)
at diningBad.cvl:20.32-32
  $parfor(int i: 0..n-1) dine(i);
                                ^

A deadlock is possible:
  Path condition: true
  Enabling predicate: false
process p0 (id=0): false
process p1 (id=1): false
process p2 (id=2): false

Call stacks:
process 0:
  main at diningBad.cvl:20.32 ";"
process 1:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"
process 2:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"

Logging new entry 0, writing trace to CIVLREP/diningBad_0.trace
Terminating search after finding 1 violation.

=== Source files ===
diningBad.cvl  (diningBad.cvl)


=== Command ===
civl verify -inputB=5 diningBad.cvl

=== Stats ===
   time (s)            : 1.43
   memory (bytes)      : 163053568
   max process count   : 3
   states              : 32
   states saved        : 26
   state matches       : 1
   transitions         : 30
   trace steps         : 21
   valid calls         : 106
   provers             : cvc4, z3
   prover calls        : 4

=== Result ===
The program MAY NOT be correct.  See CIVLREP/diningBad_log.txt
```

The output indicates that a deadlock has been found and a
counterexample has been produced and saved. We can examine the
counterexample, but it is more helpful to work with a minimal
counterexample, i.e., a deadlocking trace of minimal length. To find a
minimal counterexample, we issue the command

```sh
civl verify -inputB=5 -min diningBad.cvl
```

which results in the output

```
CIVL v1.18+ of 2018-12-28 -- http://vsl.cis.udel.edu/civl

Violation 0 encountered at depth 21:
CIVL execution violation (kind: DEADLOCK, certainty: PROVEABLE)
at diningBad.cvl:20.32-32
  $parfor(int i: 0..n-1) dine(i);
                                ^

A deadlock is possible:
  Path condition: true
  Enabling predicate: false
process p0 (id=0): false
process p1 (id=1): false
process p2 (id=2): false

Call stacks:
process 0:
  main at diningBad.cvl:20.32 ";"
process 1:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"
process 2:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"

Logging new entry 0, writing trace to CIVLREP/diningBad_0.trace
Restricting search depth to 20

Violation 1 encountered at depth 16:
CIVL execution violation (kind: DEADLOCK, certainty: PROVEABLE)
at diningBad.cvl:20.32-32
  $parfor(int i: 0..n-1) dine(i);
                                ^

A deadlock is possible:
  Path condition: true
  Enabling predicate: false
process p0 (id=0): false
process p1 (id=1): false
process p2 (id=2): false

Call stacks:
process 0:
  main at diningBad.cvl:20.32 ";"
process 1:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"
process 2:
  dine at diningBad.cvl:12.4-8 "$when" called from
  _par_proc0 at diningBad.cvl:20.25-28 "dine"

New log entry is equivalent to previously encountered entry 0
Length of new trace (16) is less than length of old (21): replacing old with new...
Restricting search depth to 15

=== Source files ===
diningBad.cvl  (diningBad.cvl)


=== Command ===
civl verify -inputB=5 -min diningBad.cvl

=== Stats ===
   time (s)            : 1.46
   memory (bytes)      : 163053568
   max process count   : 6
   states              : 96
   states saved        : 77
   state matches       : 2
   transitions         : 91
   trace steps         : 64
   valid calls         : 334
   provers             : cvc4, z3
   prover calls        : 4

=== Result ===
The program MAY NOT be correct.  See CIVLREP/diningBad_log.txt
```

The output indicates that a minimal counterexample consists of 16
execution steps.  It was the second and shortest trace found. It was
deemed equivalent to the earlier traces and hence the earlier ones
were discarded and only this one saved. We can replay the trace with
the command

```sh
civl replay -showTransitions diningBad.cvl
```

which results in the output

```
CIVL v1.18+ of 2018-12-28 -- http://vsl.cis.udel.edu/civl

Initial state:

State (id=9)
| Path condition
| | true
| Dynamic scopes
| | dyscope d0 (parent=NULL, static=1)
| | | variables
| | | | B = NULL
| | | | n = NULL
| | | | forks = NULL
| Process states
| | process 0
| | | call stack
| | | | Frame[function=main, location=0, diningBad.cvl:1.15 "4", dyscope=d0]

Executed by p0 from State (id=9)
  0->1: B=5 at diningBad.cvl:1.0-15 "$input int B = 4"
  1->2: n=InitialValue(n) [n:=X_n] at diningBad.cvl:2.0-11 "$input int n"
  2->3: $assume((2<=X_n)&&(X_n<=5)) at diningBad.cvl:3.0-20 "$assume(2<=n && n ... )"
  3->4: forks=InitialValue(forks) [forks:=(boolean[X_n]) ($lambda i: int. false)] at diningBad.cvl:5.0-13 "_Bool forks[n]"
--> State (id=18)

Step 1: Executed by p0 from State (id=18)
  4->5: $elaborate_domain(($domain(1))(0..X_n-1#1)) [$assume(0==(X_n - 2))] at diningBad.cvl:19.14-19 "0..n-1"
--> State (id=22)

Step 2: Executed by p0 from State (id=22)
  5->6: LOOP_BODY_ENTER (guard: ($domain(1))(0..1#1) has next for (NULL)) at diningBad.cvl:19.14-19 "0..n-1"
  6->7: NEXT of (NULL) in ($domain(1))(0..1#1) [i:=0] at diningBad.cvl:19.2-5 "$for"
--> State (id=26)

Step 3: Executed by p0 from State (id=26)
  7->5: forks[0]=true at diningBad.cvl:19.22-civlc.cvh:10.14 "forks[i] = 1"
--> State (id=29)

Step 4: Executed by p0 from State (id=29)
  5->6: LOOP_BODY_ENTER (guard: ($domain(1))(0..1#1) has next for (0)) at diningBad.cvl:19.14-19 "0..n-1"
  6->7: NEXT of (0) in ($domain(1))(0..1#1) [i:=1] at diningBad.cvl:19.2-5 "$for"
--> State (id=33)

Step 5: Executed by p0 from State (id=33)
  7->5: forks[1]=true at diningBad.cvl:19.22-civlc.cvh:10.14 "forks[i] = 1"
--> State (id=36)

Step 6: Executed by p0 from State (id=36)
  5->8: LOOP_BODY_EXIT (guard: !($domain(1))(0..1#1) has next for (1)) at diningBad.cvl:19.14-19 "0..n-1"
--> State (id=38)

Step 7: Executed by p0 from State (id=38)
  8->9: $elaborate_domain(($domain(1))(0..1#1)) [$assume(true)] at diningBad.cvl:20.17-22 "0..n-1"
  9->10: $parfor(i0: ($domain(1))(0..1#1)) $spawn _par_proc0(i0) at diningBad.cvl:20.2-8 "$parfor"
  10->11: _civl_ir1=0 at diningBad.cvl:20.32 ";"
--> State (id=52)

Step 8: Executed by p0 from State (id=52)
  11->12: LOOP_BODY_ENTER (guard: 0<2) at diningBad.cvl:20.32 ";"
--> State (id=54)

Step 9: Executed by p1 from State (id=54)
  23->15: dine(0) at diningBad.cvl:20.25-31 "dine(i)"
  15->16: left=0 at diningBad.cvl:8.2-14 "int left = id"
  16->17: right=(0+1)%2 [right:=1] at diningBad.cvl:9.2-25 "int right = (id  ... n"
--> State (id=61)

Step 10: Executed by p1 from State (id=61)
  17->18: LOOP_BODY_ENTER (guard: 1!=0) at diningBad.cvl:10.9 "1"
--> State (id=63)

Step 11: Executed by p2 from State (id=63)
  23->15: dine(1) at diningBad.cvl:20.25-31 "dine(i)"
  15->16: left=1 at diningBad.cvl:8.2-14 "int left = id"
  16->17: right=(1+1)%2 [right:=0] at diningBad.cvl:9.2-25 "int right = (id  ... n"
--> State (id=70)

Step 12: Executed by p2 from State (id=70)
  17->18: LOOP_BODY_ENTER (guard: 1!=0) at diningBad.cvl:10.9 "1"
--> State (id=72)

Step 13: Executed by p1 from State (id=72)
  18->19: forks[0]=false at diningBad.cvl:11.24-civlc.cvh:12.15 "forks[left] = 0"
--> State (id=75)

Step 14: Executed by p2 from State (id=75)
  18->19: forks[1]=false at diningBad.cvl:11.24-civlc.cvh:12.15 "forks[left] = 0"
--> State (id=78)

Step 15:
State (id=78)
| Path condition
| | true
| Dynamic scopes
| | dyscope d0 (parent=NULL, static=1)
| | | variables
| | | | B = 5
| | | | n = 2
| | | | forks = {[0]=false, [1]=false}
| | dyscope d1 (parent=d0, static=4)
| | | variables
| | | | i = 1
| | | | __LiteralDomain_counter0__ = NULL
| | dyscope d2 (parent=d0, static=7)
| | | variables
| | | | _dom_size0 = 2
| | | | _par_procs0 = {[0]=p1, [1]=p2}
| | dyscope d3 (parent=d0, static=8)
| | | variables
| | | | i = 0
| | dyscope d4 (parent=d0, static=8)
| | | variables
| | | | i = 1
| | dyscope d5 (parent=d2, static=9)
| | | variables
| | | | _civl_ir1 = 0
| | dyscope d6 (parent=d5, static=10)
| | | variables
| | dyscope d7 (parent=d0, static=3)
| | | variables
| | | | id = 0
| | dyscope d8 (parent=d7, static=12)
| | | variables
| | | | left = 0
| | | | right = 1
| | dyscope d9 (parent=d0, static=3)
| | | variables
| | | | id = 1
| | dyscope d10 (parent=d9, static=12)
| | | variables
| | | | left = 1
| | | | right = 0
| Process states
| | process 0
| | | call stack
| | | | Frame[function=main, location=12, diningBad.cvl:20.32 ";", dyscope=d6]
| | process 1
| | | call stack
| | | | Frame[function=dine, location=19, diningBad.cvl:12.4-8 "$when", dyscope=d8]
| | | | Frame[function=_par_proc0, location=23, diningBad.cvl:20.25-28 "dine", dyscope=d3]
| | process 2
| | | call stack
| | | | Frame[function=dine, location=19, diningBad.cvl:12.4-8 "$when", dyscope=d10]
| | | | Frame[function=_par_proc0, location=23, diningBad.cvl:20.25-28 "dine", dyscope=d4]

Violation of Deadlock found in (id=78):
A deadlock is possible:
  Path condition: true
  Enabling predicate: false
process p0 (id=0): false
process p1 (id=1): false
process p2 (id=2): false

Trace ends after 15 trace steps.
Violation(s) found.

=== Source files ===
diningBad.cvl  (diningBad.cvl)


=== Command ===
civl replay -showTransitions diningBad.cvl

=== Stats ===
   time (s)            : 1.44
   memory (bytes)      : 163053568
   max process count   : 3
   states              : 27
   valid calls         : 100
   provers             : cvc4, z3
   prover calls        : 4
```

The output indicates that a deadlock has been found involving 2
philosophers.  After the initialization sequence, each philosopher
picks up her left fork.


## Verifying Sequential C Programs

Since almost anything you can do in sequential C is also legal CIVL-C,
there is not much you have to do to apply the verifier to C programs.

The verifier requires a complete program --- i.e., there must be a
main function --- and there is usually some set-up that you want to do
for CIVL that is different than what you want the program to do in
normal use.  For this reason, there is a preprocessor object-like
macro `_CIVL` which is defined when using the CIVL verifier.  This
allows you to insert some CIVL-C code that will be used for
verification, without interfering with the normal compilation and use
of the program.  Consider the following example, `sum.c`:

```c
#include <assert.h>
#include <stdio.h>
#ifdef _CIVL
#include <civlc.cvh>
$input int B=5, N;
$assume(1<=N && N<=B);
#else
#define N 100
#endif
int sum=0;
int main() {
  for (int i = 1; i <= N; i++) sum += i;
  printf("N=%d, sum = %d\n", N, sum);
  assert(sum == (N+1)*N/2);
}
```

The program can be compiled and executed as usual...

```sh
$ cc -o sum sum.c
$ ./sum
N=100, sum = 5050
$
```

...and it can be verified using CIVL:

```sh
$ civl verify sum.c
CIVL v1.18+ of 2018-12-28 -- http://vsl.cis.udel.edu/civl
N=5, sum = 15
N=4, sum = 10
N=3, sum = 6
N=2, sum = 3
N=1, sum = 1

=== Source files ===
sum.c  (sum.c)


=== Command ===
civl verify sum.c

=== Stats ===
   time (s)            : 2.26
   memory (bytes)      : 163053568
   max process count   : 1
   states              : 51
   states saved        : 31
   state matches       : 0
   transitions         : 50
   trace steps         : 16
   valid calls         : 54
   provers             : cvc4, z3
   prover calls        : 13

=== Result ===
The standard properties hold for all executions.
$
```

Another approach for separating the CIVL driver code from the "real"
program is to place these in separate translation units.  In the
following example, a toy library "sumlib" has been implemented using a
header file `sumlib.h` and an implementation `sumlib.c`:

```c title="sumlib.h"
int sum(int n);
```

```c title="sumlib.c"
#include "sumlib.h"
int sum(int n) {
  int result = 0;
  for (int i=1; i<=n; i++) result += i;
  return result;
}
```

A simple test has been implemented in a separate translation unit
named `sumlib_test.c`.  The translation units can be compiled, linked,
and executed, in the usual way.

```c title="sumlib_test.c"
#include <stdio.h>
#include <assert.h>
#include "sumlib.h"
#define N 100
int main() {
  int result = sum(N);
  printf("N=%d, sum = %d\n", N, result);
  assert(result == (N+1)*N/2);
}
```

```sh
$ cc sumlib_test.c sumlib.c
$ ./a.out
N=100, sum = 5050
```

Finally, a CIVL verification driver is provided in another translation
unit, `sumlib_driver.cvl`.  The CIVL verifier can be applied to the
whole program composed of the two translation units
`sumlib_driver.cvl` and `sumlib.c`:

```civl title="sumlib_driver.cvl"
#include <stdio.h>
#include "sumlib.h"
$input int B=5, N;
$assume(1<=N && N<=B);
int main() {
  int result = sum(N);
  printf("N=%d, sum = %d\n", N, result);
  $assert(result == (N+1)*N/2);
}
```

```sh
$ civl verify sumlib_driver.cvl sumlib.c
CIVL v1.18+ of 2018-12-28 -- http://vsl.cis.udel.edu/civl
N=5, sum = 15
N=4, sum = 10
N=3, sum = 6
N=2, sum = 3
N=1, sum = 1

=== Source files ===
sumlib_driver.cvl  (sumlib_driver.cvl)
sumlib.h  (sumlib.h)
sumlib.c  (sumlib.c)


=== Command ===
civl verify sumlib_driver.cvl sumlib.c

=== Stats ===
   time (s)            : 2.78
   memory (bytes)      : 163053568
   max process count   : 1
   states              : 47
   states saved        : 32
   state matches       : 0
   transitions         : 46
   trace steps         : 16
   valid calls         : 49
   provers             : cvc4, z3
   prover calls        : 13

=== Result ===
The standard properties hold for all executions.
```

There are limitations to the application of CIVL to C programs.
Support for the standard library is only partial.  Small bounds will
have to be placed on many parameters in order for CIVL verification to
terminate (or terminate in a reasonable amount of time).

## Verifying C/MPI Programs

<!-- TODO: Link to main [[wiki:MPI Documentation|MPI Documentation.]] -->

CIVL can verify C/MPI programs that use a subset of MPI.  The
instructions for sequential programs apply equally to MPI programs.
In addition, one must specify either (1) the number of processes for
the MPI program, or (2) an upper and a lower bound on the number of
processes for the MPI program.

In the following example, the C/MPI program `ring.c` is verified for exactly 5 processes:

```sh
civl verify -input_mpi_nprocs=5 ring.c
```

In the following example, `ring.c` is verified for any number of
processes between 2 and 5, inclusive:

```sh
civl verify -input_mpi_nprocs_lo=2 -input_mpi_nprocs_hi=5 ring.c
```



## Verifying C/OpenMP Programs

<!-- Link to main [[wiki:OpenMP Documentation|OpenMP Documentation.]] -->

CIVL uses an input variable `omp_thread_max` for verifying OpenMP
programs.  It must be specified on the command line, e.g.,

```sh
civl verify -input_omp_thread_max=3 sum_omp.c
```

Upon entering an OpenMP parallel region, CIVL will
nondeterministically choose an integer between 1 and `omp_thread_max`,
and create a thread team consisting of that number of threads.  If
`omp_thread_max` is not specified, then the program must explicitly
specify the number of threads for each parallel region.

By default, CIVL attempts to simplify an OpenMP program by replacing
parallel code with sequential code when it can determine that the two
are equivalent.  In the best case, this can remove all of the OpenMP,
resulting in a sequential program.  The option `-ompNoSimplify` can be
used to disable such simplification.  Another option,
`-ompLoopDecomp=X` can be used to specify the loop decomposition
strategy, where `X` is one `ALL` (the default), `ROUND_ROBIN`, or
`RANDOM`.
