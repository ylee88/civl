# A Challenge Exercise for Twente

## A Dissemination Barrier

Designing efficient barriers is a big problem in concurrency theory.
A barrier is a synchronization operation that is invoked by all threads or processes
in a concurrent system.  The defining property is that no process can leave
the barrier until every process has entered the barrier.  Of course, a barrier
should not deadlock --- once all processes have entered, they should all
be able to leave.   A barrier should also be re-useable, i.e., it can be invoked
more than once.

The dissemination barrier works as follows.   There are n processes, numbered
`0 .. n-1`.    The order is treated as cyclical.   The protocol goes through
`ceil(log2(n))` stages, `i = 0, 1, ...`   At stage $i$, each process sends a message to the process
$2^{i}$ to its "right", and waits to receive a message from the process $2^{i}$ to its "left".
The content of the message is irrelevant (and can be empty).

**Challenge 1**: Design a dissemination barrier using MPI.  Verify it up to some
bound on the number of processes using CIVL.

**Challenge 2**: Design a dissemination barrier using semaphores.  Verify it up
to some bound on the number of threads using CIVL.

In both cases, there should be a function named `barrier` (possibly with
parameters) that all processes/threads call in order to participate in a barrier.
The function could use global variables.

In both cases, part of the challenge is to come up with an appropriate driver
that tests the barrier.   Try to develop the most universal (challenging) driver
you can.   Can you make one that guarantees the barrier is correct?

## CIVL-C Reference

For installation instructions: [CIVL Manual: Introduction](introduction.md).

Almost everything from standard sequential C is allowed.
The standard library is partially implemented, including:

`printf`, `assert`, `malloc`, `free`, ...

Be sure to include the appropriate headers.

In standard C, function definitions can occur only in file scope.
In CIVL-C, they can occur in any scope.

* `$input decl`

    Declares a variable in file scope to be an input variable.  An input variable
    `X` is initialized according to the following protocol: (1) if a value for `X`
    is specified on the command line via `-inputX=val`, then `val` is the
    initial value; (2) otherwise, if an initializer is present in the declaration, the
    initializer is used; (3) otherwise, `X` is assigned an unconstrained value
    of its type---when using symbolic execution, it is assigned a fresh symbolic
    constant.

* `$when (expr) stmt`

    a guarded command.  Blocks until `expr`
    is true.  Executes the first atomic action of `stmt` atomically
    with the evaluation of `expr` to true.   Example: `$when (s>0) s--`
    defines Dijkstra's P operation on semaphores.   The V operation
    is just `s++`.  Note that `s--` and `s++` are both atomic in CIVL-C.

* `_Bool`

    The Boolean type.  Values: 0 and 1.  This is the same as C11.

* `$proc`

    the process type.  A value of this type refers to a process.

* `$spawn stmt`

    creates a new process executing the statement.  Returns a value of type `$proc`
    referencing the new process.

* `$wait($proc p)`

    blocks until process p terminates

* `$parfor (int i:a..b) stmt`

    spawns threads, one for each i in the range a..b, each executing `stmt`.
    Waits for them to terminate.

* `$assert(expr)`

    An assertion.  This is treated exactly the same as C's `assert`, except with
    the latter you must include `assert.h`.    CIVL-C has a richer assertion
    language that C---e.g., you can use quantifiers.

* `$assume(expr)`

    Assumes this expression is true, i.e., if the expression is not true, the
    the execution is not considered to be a valid execution of the program.

* `$forall` and `$exists`

    Mean what you think.   Here are some examples to show the syntax:

    * `$forall (double x | x>1) x>0` -- for all doubles `x` that are greater than 1, `x` is greater than 0
    * `$forall (int i | a<=i && i<=b) p(i)` -- for all i between a and b (inclusive), `p(i)` holds
    * `$forall (int i:a..b) p(i)`  -- same as above, but more convenient

* `int $choose_int(int n)`

    Returns an integer nondeterministically chosen from `0 .. n-1`.

* `$choose { stmt1 stmt2 ... }`

    Structural nondeterminism.  Chooses one of the enabled statements
    nondeterministically.  The statements are often guarded using `$when`.

## MPI Reference

### Constants

* `MPI_COMM_WORLD` (type `MPI_Comm`)
* `MPI_STATUS_IGNORE` (type `MPI_Status*`)
* `MPI_ANY_SOURCE` (type `int`)
* `MPI_ANY_TAG` (type `int`)
* `MPI_INT` (type `MPI_Datatype`)
* `MPI_DOUBLE` (type `MPI_Datatype`)

### Functions

* `MPI_Init(NULL, NULL)`
* `MPI_Finalize()`

`MPI_Init` must be called before other MPI functions.   `MPI_Finalize`
must be called before termination and after all other MPI functions.

* `MPI_Comm_size(MPI_Comm comm, int *nprocs)`
* `MPI_Comm_rank(MPI_Comm comm, int *rank)`

Get the number of processes in the communicator, and the "rank" (ID
number) of this process within the communicator.

* `int MPI_Send(const void *buf, int count, MPI_Datatype datatype, int dest, int tag, MPI_Comm comm)`

Sends a message to the process of rank dest.
You can use `NULL` for `buf` if `count` is 0 --- an empty message.
Note that `MPI_Send` may be forced to synchronize --- i.e., it can block
until the receiving process reaches a matching receive statement.

* `int MPI_Recv(void *buf, int count, MPI_Datatype datatype, int source, int tag,  MPI_Comm comm, MPI_Status *status)`

Receives a message from the specified source process.
`source` can be `MPI_ANY_SOURCE` --- to receive from any source ("wildcard").
status can be `MPI_STATUS_IGNORE`, if you don't care about the status.
Otherwise, declare a variable of type `MPI_Status` and pass a pointer to it.

```c
int MPI_Sendrecv(const void *sendbuf, int sendcount, MPI_Datatype sendtype,
                 int dest, int sendtag,
                 void *recvbuf, int recvcount, MPI_Datatype recvtype,
                 int source, int recvtag,
                 MPI_Comm comm, MPI_Status *status);
```

Combines one `MPI_Send` operation and one `MPI_Recv` operation into a
single command.  It behaves as if the two operations execute
concurrently in separate threads.  Used to avoid deadlocks that could
result if all processes do `MPI_Send; MPI_Recv`.


## A two-thread barrier using semaphores in CIVL-C

```civl
#include <stdio.h>

int s1=0, s2=0;
#define sem_wait(s) $when (s>0) s--;
#define sem_post(s) s++;
const int N=10;
int i1=0, i2=0;

void f1() {
  while (i1<N) {
    printf("thread 1 at iteration %d\n", i1);
    fflush(stdout);
    i1++;
    sem_post(s1);
    sem_wait(s2);
    $assert(i1==i2);
    sem_post(s1);
    sem_wait(s2);
  }
}

void f2() {
  while (i2<N) {
    printf("thread 2 at iteration %d\n", i2);
    fflush(stdout);
    i2++;
    sem_post(s2);
    sem_wait(s1);
    $assert(i1==i2);
    sem_post(s2);
    sem_wait(s1);
  }
}

int main() {
  $proc t1 = $spawn f1(), t2 = $spawn f2();
  $wait(t1);
  $wait(t2);
}
```

## An example MPI program

```c
#include <mpi.h>
#include <stdio.h>
#include <assert.h>

#define FROMRIGHT 0
#define FROMLEFT  1

int main() {
  int rank, size;
  int recv;
  int left, right;

  MPI_Init(NULL, NULL);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  MPI_Comm_size(MPI_COMM_WORLD, &size);
  left = (rank == 0) ? size - 1 : rank - 1;
  right = (rank == (size - 1)) ? 0 : rank + 1;
  MPI_Sendrecv(&rank, 1, MPI_INT, left, FROMRIGHT, &recv, 1, MPI_INT,
	       right, FROMRIGHT, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
  assert(recv == right);
  MPI_Sendrecv(&rank, 1, MPI_INT, right, FROMLEFT, &recv, 1,
  	       MPI_INT, left, FROMLEFT, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
  assert(recv == left);
  MPI_Finalize();
  return 0;
}
```


## Verifying C/MPI Programs

CIVL can verify C/MPI programs that use a subset of MPI.    The instructions for sequential programs apply equally to MPI programs.  In addition, one must specify either (1) the number of processes for the MPI program, or (2) an upper and a lower bound on the number of processes for the MPI program.

In the following example, the C/MPI program `ring.c` is verified for exactly 5 processes:

```sh
civl verify -input_mpi_nprocs=5 ring.c
```

In the following example, `ring.c` is verified for any number of processes between 2 and 5, inclusive:

```sh
civl verify -input_mpi_nprocs_lo=2 -input_mpi_nprocs_hi=5 ring.c
```


