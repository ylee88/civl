# CIVL Command Line Interface

## `-check<X>` CLI Options

A large subset of the errors that CIVL checks for may be toggled on or off using the command line. In some cases (currently just checking deadlocks) there is more customization. All errors types which may be configured have a corresponding command line option of the form `-check<X>` where `<X>` is a shortened name of the error class. Below is the full list of such options with a brief description of the error class the option controls. In all cases except where noted, the option is a boolean value which defaults to `true` if not specified on the command line.

* `-checkAssertion` : Check for `$assert` violations.
* `-checkCommErr` : Check for misuse of `$comm` and related structures/functions.
* `-checkConstWrite` : Check for writes to constant variables.
* `-checkDeadlock` : Check deadlocks. This option is not boolean and instead takes the following values:
  * `absolute`(default) - Checks for "absolute deadlocks" which occur when not every process has terminated but not process has an enabled statement.
  * `potential` - Checks for "potential deadlocks" which occur when not every process has terminated but the only enabled statements are sends (i.e. `$comm_enqueue`s) with no corresponding receives.
  * `none` - Do not consider deadlocks to be errors.
* `-checkDivisionByZero` : Checks for divisions by zero.
* `-checkInputWrite` : Check for writes to `$input` variables.
* `-checkInvalidCast` : Check for invalid casts.
* `-checkMallocErr` : Check for malloc errors.
* `-checkMemManageErr` : Check for memory management errors.
* `-checkMemoryLeak` : Check for memory leaks.
* `-checkMpiErr` : Check for MPI usage errors.
* `-checkOutOfBounds` : Check for out of bound errors.
* `-checkOutputRead` : Check for reads from `$output` variables.
* `-checkPointerErr` : Check for pointer errors.
* `-checkProcLeak` : Check for process leaks.
* `-checkSeqErr` : Check for misuse of `$seq` and related functions.
* `-checkTermination` : Check for non-termination.
* `-checkUndefVal` : Check use of undefined values.
* `-checkUnionErr` : Check for reading wrong field of a union.
