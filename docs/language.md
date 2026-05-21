# CIVL-C Language Manual

## Types {#sec:types}

### The Boolean type {#boolean-type}

The boolean type is denoted `_Bool`, as in C. Its values are 0 and 1, which are also denoted by `$false` and `$true`, respectively.
One may also include the standard C header `stdbool.h`, which defines `false` and `true` (also as 0 and 1), and defines the type
`bool` to be an alias for `_Bool`.

### The integer type {#int-types}

There is one integer type, corresponding to the mathematical integers.
Currently, all of the C integer types `int`, `long`, `unsigned int`, `short`, etc., are mapped to the CIVL integer type.
[This is expected to change.]

### The real type {#real-types}

There is one real type, corresponding to the mathematical real numbers. Currently, all of the C real types `double`, `float`, etc., are mapped to the CIVL real type.
[This is expected to change.]

### The process type `$proc` {#proc-type}

This is a primitive object type and functions like any other primitive C type (e.g., `int`).
An object of this type refers to a process. It can be thought of as a process ID, but it is not an integer and cannot be cast to one.
Certain expressions take an argument of `$proc` type and some return something of `$proc` type.
The operators `==` and `!=` may be used with two arguments of type `$proc` to determine whether the two arguments refer to the same process.
The constant `$self` has `$proc` type and refers to the process evaluating this expression;  constant `$proc_null` has `$proc` type and refers to no process.

### The scope type `$scope` {#scope-type}

An object of this type is a reference to a dynamic scope.
Several constants, expressions, and functions dealing with the `$scope` type are also provided.
The `$scope` type is like any other object type.
It may be used as the element type of an array, a field in a structure or union, and so on.
Expressions of type `$scope` may occur on the left or right-hand sides of assignments and as arguments in function calls just like any other expression.
Two different variables of type `$scope` may be aliased, i.e., they may refer to the same dynamic scope.

### Domain types: `$domain` and `$domain(n)` {#domain-type}

A domain type is used to represent a set of tuples of integer values.
Every tuple in a domain object has the same arity (i.e., number of components).
The arity must be at least 1, and is called the dimension of the domain object.
For each integer constant expression n, there is a type `$domain(n)`, representing domains of dimension n.
The universal domain type, denoted `$domain`, represents domains of all positive dimensions, i.e., it is the union over all n ≥ 1 of `$domain(n)`.
In particular, each `$domain(n)` is a subtype of `$domain`.
There are expressions for specifying domain values.
Certain statements use domains, such as the [domain iteration statement](#for) `$for`.

### The range type `$range` {#range-type}

An object of this type represents an ordered set of integers.
Ranges are typically used as a step in constructing domains.
They can also be used in quantified expressions to specify the domain of a bound variable (see [$forall and $exists](#boolean-expressions)).

## Type qualifiers {#qualifiers}

### Declaring input and output variables: `$input` and  `$output` {#input-output}

The declaration of a variable in the root scope may include the type qualifier `$input`, e.g.,
```civl
$input int N;
```
This declares the variable to be an input variable, i.e., one which is considered to be an input to the program.
Such a variable is initialized with an arbitrary (unconstrained) value of its type.
When using symbolic execution to verify a program, such a variable will be assigned a unique symbolic constant of its type.
In contrast, variables in the root scope which are not input variables will instead be initialized with the “undefined” value.
Reading an undefined value is erroneous.

!!! note

    The model checker attempts to catch such errors but currently
    does not do so for arrays, which are always initialized with unconstrained values.

In addition, input variables may only be read, never written to; an attempt to write to an input variable is also flagged as an error.

Alternatively, it is possible to specify a particular concrete value for an input variable, either on the command line, e.g.,
```sh
civl verify -inputN=8 ...
```
or by including an initializer in the declaration, e.g.
```civl
$input int N=8;
```
The protocol for initializing an input variable is the following: if a command line value is specified, it is used.
Otherwise, if an initializer is present, it is used.   Otherwise, the variable is assigned an arbitrary value of its type.

A variable in the root scope may be declared with `$output` to declare it to be an output variable.
Output variables may only be written to, never read.
They are used primarily in functional equivalence checking.

Input and output variables play a key role when determining whether two programs are functionally equivalent.
Two programs are considered functionally equivalent if, whenever they are given the same inputs (i.e., corresponding `$input`
variables are initialized with the same values) they will produce the same outputs (i.e., corresponding `$output` variables will
end up with the same values at termination).

### Abstract (uninterpreted) functions: `$abstract` {#abstract}

An abstract function declares a function without a body.
An abstract function is declared using a standard function prototype with the function qualifier `$abstract`, e.g.,
```civl
$abstract int f(int x);
```
An abstract function must have a non-`void` return type and take at least one parameter.
An invocation of an abstract function is an expression and can be used anywhere an expression is allowed.
The interpretation is an "uninterpreted function".
A unique symbolic constant of function type will be created, corresponding to the abstract function, and invocations are represented
as applications of the uninterpreted function to the arguments.

### Atomic functions: `$atomic_f` {#atomic_f}

A function is declared atomic using the function qualifier `$atomic_f`, e.g.,
```civl
$atomic_f int f(int x) {
  ...
}
```
A call to such a function executes as a single atomic step, i.e., without interleaving from other processes.
Hence, this is only relevant for concurrent programs.
Declaring a function to be atomic is almost equivalent to placing `$atomic{ ... }` around the function body.
The difference is that in the latter case, the call to the function and the execution of the body are executed in two atomic steps, i.e.,
after the activation frame is pushed onto the call stack, another process could execute before the first process obtains the atomic lock and executes its body.
For an atomic function, the entire sequence of events happens in one atomic step.

An atomic function must have a definition; in particular, neither a system function nor an abstract function may be declared using `$atomic_f`.

The guard of an atomic function is taken to be the guard of the function's body.  For example, if `f` is defined
```civl
$atomic_f int f(int x) {
  $when (x>0);
  ...
}
```
then a call to `f` will block if the argument used in the call is not positive—no activation frame is pushed onto the stack unless the guard is true.

### System functions: `$system` {#system}

A system function is one in which the definition of the function is not provided in CIVL-C code, but is implemented instead in a certain Java class.
A system function is declared by adding the function qualifier `$system` to a function prototype.
Invocation of a system function always takes place in a single atomic step.

A system function may have a guard, which is specified in the function contract using a `$executes_when` clause.
Unless constrained by its contract or other qualifiers, a system function may modify the state in an arbitrary way.

### Pure functions: `$pure` {#pure}

A system or atomic function may be declared to be `$pure`, e.g.,
```civl
$pure $system double sin(double x);
$pure $atomic_f double mysin(double x) {
  return  x - x*x*x/6.;
}
```
This means that the function is a mathematical function of its arguments only.
I.e., an invocation of the function has no side-effects and the return value depends on the arguments only.
(If called twice with the same arguments, it will return the same value, regardless of any differences in the state).
The user is responsible for ensuring that a function declared pure actually is pure.
If this is not the case, the model checker may produce incorrect results.

## Expressions {#expressions}

### Boolean expressions, `=>`, `$forall`, `$exists` {#boolean-expressions}

CIVL-C provides the boolean constants `$true` and `$false`, which are simply defined as 1 and 0, respectively.
CIVL-C is, after all, an extension of C.
A program may also include the standard C library header file `stdbool.h`, which defines `true` and `false` in the exact same way.


In addition to the standard logical operators `&&`, `|`, and `!`, CIVL-C provides `=>` (implies).
`p => q` is equivalent to `!(p) | q` (and has the same short-circuit semantics).

A universally quantified formula has the form

> `$forall` `(` *variable-decls* (`;` *variable-decls* )\* (`|` *restriction*)? `)` *expr*

where

* *variable-decls* has one of the forms
    - *type* *identifier* (`,` *identifier*)\*
    - `int` *identifier* `:` *range*
* *type* is a type name (e.g., `int` or `double`),
* *identifier* is the name of a bound variable,
* *range* is an expression of `$range` type,
* *restriction* is a formula (boolean expression) which expresses some restriction on the values that the bound variable can take, and
* *expr* is a formula.
The syntax for existential quantification is the same, with `$exists` in place of `$forall`.

We will call any assignment of values to the bound variables of a quantified expression a "candidate assignment" if it satisfies *restriction* and each bound variable with an associated *range* is given a value contained in this *range*. The universally quantified formula holds iff *expr* holds under all candidate assignments. Similarly, the existentially quantified formula holds iff there exists a candidate assignment for which *expr* holds.

Examples:
```civl
int a[3], b[3][2];
int main() {
  int n=3, m=2;
  $assert($forall (int i | 0<=i && i<n) a[i]==0); // all elements of a are 0
  $assert($forall (int i: 0..n-1) a[i]==0); // same as above
  $assert($forall (int i: 0..n-1 | i%2==0) a[i]==0); // even elements are 0
  $assert($forall (int i: 0..n-1#2) a[i]==0); // same as above
  $assert($forall (int i: 0..n-1; int j: 0..m-1) b[i][j]==0); // all elements of b are 0
  $assert($forall (int i: 0..n-1; int j | 0<=j && j<m) b[i][j]==0); // same
  $assert($forall (int i: 0..n-1; int j: 0..m-1 | i<j ) b[i][j]==0); // lower triangle is 0
  $assert($forall (int i,j | 0<=i && i<n && 0<=j && j<m) b[i][j]==0); // all elements of b are 0
  $assert($exists (int i | 0<=i && i<n) a[i]==0); // existential: some element of a is 0
  $assert($forall (int i: 0..n-1) $exists (int j: 0..i) a[j]<=a[i]); // nested quantification
}
```

### Domain literals {#domain-literals}

An expression of the form

> `(` `$domain` `)`  `{` $r_{1}$ `,` ... `,` $r_{n}$ `}`

where $r_{1}$, ..., $r_{n}$ are *n* expressions of type `$range`, is a *Cartesian domain expression*.
It represents the domain of dimension *n* which is the Cartesian product of the *n* ranges,
i.e., it consists of all *n*-tuples ($x_{1}$, ..., $x_{n}$) where $x_{1} \in r_{1}$, ..., $x_{n} \in r_{n}$.
The order on the domain is the dictionary order on tuples.
The type of this expression is `$domain(n)`.
When a Cartesian domain expression is used to initialize an object of domain type, the `($domain)` may be omitted.
For example:
```civl
$domain(3) dom = { 0..3, r2, 10..2#-2 };
```

### This scope: the `$here` scope expression {#here}
Expression of type `$scope`, evaluating to the dynamic scope in which the evaluation takes place.

### The null process reference: `$proc_null` {#proc_null}
The null process constant.
Similar to the NULL pointer, this gives an object of `$proc` type a defined value, and can be used in `==` and `!=` expressions.
It cannot be used as the argument to `$wait` or `$waitall`.

### The root scope constant: `$root` {#root}
Constant of type `$scope`, the root dynamic scope.

### Range literals: *a*`..`*b* and *a*`..`*b*`#`*c* {#range-literals}

A range literal has the form

> *expr* `..` *expr* ( `#` *expr* )?

The two or three sub-expressions (the third is optional) have integer type and the type of the entire expression is `$range`.

The range literal *a*`..`*b* represents the range consisting of the integers *a*, *a*+1, ..., *b* (in that order).

The range literal *a*`..`*b*`#`*c* is interpreted as follows.
If *c* is positive, it represents the range consisting of *a*, *a*+*c*, *a*+2*c*, ...,  up to and possibly including *b*.
To be precise, the infinite sequence is intersected with the set of integers less than or equal to *b*.
If *c* is negative, the expression represents the range consisting of *b*, *b*+*c*, *b*+2*c*, ..., down to and possibly including *a*.
Precisely, the infinite sequence is intersected with the set of integers greater than or equal to *a*.

### The scope of an expression: `$scopeof` {#scopeof}

The syntax is

> `$scopeof` `(` *expr* `)`

where *expr* is an expression that can occur on the left hand side of an assignment operator (i.e., an lvalue).
It evaluates to the dynamic scope containing the object specified by *expr*.
The following example illustrates the semantics of the `$scopeof` operator. All of the assertions hold:
```civl
{
  $scope s1 = $here;
  int x;
  double a[10];
  {
    $scope s2 = $here;
    int *p = &x;
    double *q = &a[4];
    assert($scopeof(x)==s1);
    assert($scopeof(p)==s2);
    assert($scopeof(*p)==s1);
    assert($scopeof(a)==s1);
    assert($scopeof(a[5])==s1);
    assert($scopeof(q)==s2);
    assert($scopeof(*q)==s1);
  }
}
```

### Scope expressions {#scope-expressions}

Let $s_{1}$ and $s_{2}$ be expressions of [type $scope](#scope-type). The following are all CIVL-C expressions of boolean type:

* $s_{1}$`==`$s_{2}$ holds iff $s_{1}$ and $s_{2}$ refer to the same dynamic scope.
* $s_{1}$`!=`$s_{2}$ holds iff $s_{1}$ and $s_{2}$ refer to different dynamic scopes.
* $s_{1}$`<=`$s_{2}$ holds iff $s_{1}$ is equal to or a descendant of $s_{2}$, i.e., $s_{1}$ is equal to or contained in $s_{2}$.
* $s_{1}$`<`$s_{2}$ holds iff $s_{1}$ is a strict descendant of $s_{2}$, i.e., $s_{1}$ is contained in $s_{2}$ and is not equal to $s_{2}$.
* $s_{1}$`>`$s_{2}$ is equivalent to $s_{2}$`<`$s_{1}$.
* $s_{1}$`>=`$s_{2}$ is equivalent to $s_{2}$`<=`$s_{1}$.

The expression $s_{1}$`+`$s_{2}$ evaluates to the lowest common ancestor of $s_{1}$ and $s_{2}$ in the dynamic scope tree.
This is the smallest dynamic scope containing both $s_{1}$ and $s_{2}$.

Each of these expressions is erroneous if $s_{1}$ or $s_{2}$ is undefined.
This error is reported by the model checker.

### This process: `$self` {#self}

This expression of `$proc` type returns a reference to the process which is evaluating this expression.
It provides a way for code to obtain the identity of the process executing the code.

### Spawning a new process: `$spawn` {#spawn}

A spawn expression is an expression with side-effects.
It spawns a new process and returns a reference to the new process, i.e., an object of [type $proc](#proc-type).
The syntax is the same as a function call with the keyword `$spawn` inserted in front:

> `$spawn` *function-expr* `(` ( *expr* ( `,` expr )\* )? `)`

Example:
```civl
$spawn f(3.14, x+2*y)
```

Typically the returned value is assigned to a variable, e.g.,
```civl
$proc p = $spawn f(i);
```
If the function `f` returns a value, that value is ignored.

## Statements {#statements}

### Atomic statements: `$atomic` {#atomic}

An atomic statement has syntax

> `$atomic` *stmt*

It indicates that *stmt* should be executed without the intervention of other processes.
For example,
```civl
$atomic {
  x=x+1;
  y=2*x+y;
}
```
In this example, *stmt* is a compound statement `{` ... `}`.  This is usually the case.

Semantics: there is a global atomic lock which is initially free.
The global atomic lock can be obtained by a process by entering an atomic statement.
Whenever the lock is held by some process, all other processes must wait until the atomic lock becomes free to continue execution.
The guard of an atomic statement is simply the guard of the first sub-statement of the atomic statement.
So, a process may only enter an atomic statement if the lock is free (or if the process already owns the lock) and the guard holds.
The process may not necessarily enter the atomic statement as soon as these conditions hold, because some other enabled process may be scheduled first.
In fact, some other process may obtain the atomic lock.
But if the enabling conditions hold and this process is scheduled, it will obtain the atomic lock and begin executing *stmt* without other processes executing.
Upon reaching the end of *stmt*, the process releases the atomic lock and exits *stmt*.

There is an exception to atomicity: if the process executing inside the atomic statement calls the [$yield function](#yield), it releases the atomic lock.
This allows other processes to execute, and even obtain the lock.
At any point at which the atomic lock is free and the first statement following the `$yield` is enabled, the original process may re-obtain
the atomic lock and continue executing atomically.

If a statement inside an atomic statement blocks, so that the process executing the atomic statement has no enabled statement, execution deadlocks.
The exception to this rule is that the first sub-statement in the atomic statement, and the first statement after a `$yield`, as described above,
may block, without necessarily causing deadlock.

Atomic blocks may be nested.
The semantics are as follows.
Each process maintains a counter which records the multiplicity with which it owns the atomic lock.
These counters are initially 0.
When a process acquires the lock, the counter is set to 1.
Each time the process attempts to enter an atomic statement when it already owns the lock, it succeeds immediately and the counter is incremented.
Each time the process leaves an atomic statement, the counter is decremented.
When the counter reaches 0, the lock is released.

Execution of a `$yield` does not change the multiplicity counter; the process releases the lock but maintains the multiplicity,
so that when the lock is re-obtained, the original multiplicity is still in place.

### Nondeterministic selection statement `$choose` {#choose}

A `$choose` statement has the form

> `$choose` `{` *stmt*+ ( `default` `:` *stmt* )? `}`

For example,

```civl
$choose {
  $when (x<n) x++;
  $when (s>0) s--;
  $default: c++;
}
```
The default clause is optional.

The guards of the statements are evaluated and among those that are true, one is chosen non-deterministically and executed.
If none are true and the default clause is present, it is chosen.
The default clause will only be selected if all guards are false.
If no default clause is present and all guards are false, the statement blocks.
Hence the implicit guard of the `$choose` statement without a default clause is the disjunction of the guards of its sub-statements.
The implicit guard of the `$choose` statement with a default clause is *true*.

#### Example
This shows how to encode a “low-level” guarded transition system:
```civl
l1:
$choose {
  $when (x>0) {x--; goto l2;}
  $when (x==0) {y=1; goto l3;}
  default: {z=1; goto l4;}
}
l2:
$choose {
  ...
}
l3:
$choose {
  ...
}
```

### Domain iteration statement: `$for` {#for}

A domain iteration statement has the form

> `$for` `(` `int` $i_{1}$ `,` ...`,` $i_{n}$ `:` *dom*`)` *S*

where $i_{1}$, ..., $i_{n}$ are *n* identifiers, *dom* is an expression of [type $domain(n)](#domain-type), and *S* is a statement.
The identifiers declare *n* variables of integer type.
Control iterates over the values of the domain, assigning the integer variables the components of the current tuple in the domain at the start of each iteration.
The scope of the variables extends to the end of *S*.
The iterations takes place in the order specified by the domain, e.g., dictionary order for a Cartesian domain.
Note that if a range expression can be used as *dom* here, it will be automatically converted to a one-dimensional domain.
For example,
```civl
$for (int i: 0..10) S
```
is equivalent to
```civl
$for (int i: ($domain(1)){0..10}) S
```

There is a also a parallel version of this construct, [$parfor](#parfor).

### Parallel for loop: `$parfor` {#parfor}

A parallel for loop statement has the form

> `$parfor` `(` `int` $i_{1}$ `,` ...`,` $i_{n}$ `:` *dom*`)` *S*

The syntax is exactly the same as that for the sequential [domain iteration statement $for](#for), only with `$parfor` replacing `$for`.

The semantics are as follows: when control reaches the loop, one process is spawned for each element of the domain.
That process has local variables corresponding to the iteration variables, and those local variables are initialized with the components
of the tuple for the element of the domain that process is assigned.
Each process executes the statement *S* in this context.
Finally, each of these processes is waited on at the end.
In particular, there is an effective barrier at the end of the loop, and all the spawned processes disappear after this point.

### Guarded commands: `$when` {#when}

A guarded command is encoded in CIVL-C using a `$when` statement:

> `$when` `(` *expr* `)` *stmt*

Semantics: all CIVL-C statements have a guard, either implicit or explicit, which specifies when the statement is enabled.
For most statements (e.g., assignments), the guard is implicitly *true*.
The `$when` statement allows one to attach an explicit guard to a statement.

At a state in which the guard evaluates to *true*, the statement is enabled, otherwise it is disabled.
A disabled statement is blocked—it will not be scheduled for execution.
When a `$when` statement is enabled and scheduled for execution, it executes by moving control to *stmt* and executing the first atomic action in *stmt*.

If *stmt* itself has a non-trivial guard, the guard of the `$when` statement is the conjunction of *expr* and the guard of *stmt*.

The evaluation of *expr* and the first atomic action of *stmt* occur as a single atomic action.
There is no guarantee that execution of *stmt* will continue atomically if it contains more than one atomic action, i.e., other processes may be scheduled.

#### Example

```civl
$when (s>0) s--;
```
This will block until `s` is positive and then decrement `s`.
The execution of `s--` is guaranteed to take place in an environment in which `s` is positive.

#### Example

```civl
$when (s>0) { s--; t++; }
```
The execution of `s--` must happen when `s>0`, but between `s--` and `t++`, other processes may execute.
The curly braces in the code above do not accomplish anything---the code is equivalent to
```civl
$when (s>0) s--; t++;
```
To make the entire statement atomic, one must use an [$atomic statement](#atomic); see the following example.

#### Example

The following statements are all equivalent:
```civl
$when (s>0) $atomic{ s--; t++; }
$atomic{ $when(s>0); s--; t++; }
$atomic{ $when(s>0) { s--; t++; } }
```
Each of these waits until `s` is positive, then from such a state, decrements `s` and increments `t` without the intervention of other processes.

#### Example

```civl
$when (s>0) $when (t>0) x=y*t;
```
This blocks until both `x` and `t` are positive then executes the assignment in that state.
It is equivalent to
```civl
$when (s>0 && t>0) x=y*t;
```


## Functions {#functions}

### Assertions: `$assert` {#assert}

The system function `$assert` has the signature
```civl
$system void $assert(_Bool expr, ...);
```
It consumes a boolean expression and any number of optional expressions which are used to construct an error message.
Note that CIVL-C boolean expressions have a richer syntax than C expressions, and may include universal or existential quantifiers.
During verification, the assertion is checked.
If it cannot be proved that it must hold, a violation is reported, and, if additional arguments are present, a specific message is printed.
These additional arguments are similar in form to those used in C’s `printf` statement:
a format string, followed by some number of arguments which are evaluated and substituted for successive codes in the format string.
For example,
```civl
$assert(x<=B, "x-coordinate %f exceeds bound %f", x, B);
```
If x=3 and B=2, the assertion is violated and CIVL prints the error message “x-coordinate 3 exceeds bound 2”.

### Assumptions: `$assume` {#assume}

The system function `$assume` has signature
```civl
$system void $assume(_Bool expr);
```
During verification, the given expression is assumed to hold. If this leads to a contradiction on some execution, that execution is simply ignored.
It never reports a violation, it only restricts the set of possible executions that will be explored by the verification algorithm.
Like an assertion call, an assume call can be used any place a statement is expected.
In addition, an assume call can be used in file scope to place restrictions on the global variables of the programs.
For example,
```civl
$input int B, N;
$assume(1<=N && N<=B);
```
declares `N` and `B` to be integer inputs and restricts consideration to inputs satisfying 1 ≤ `N` ≤ `B`.

### Temporary assumptions: `$assume_push` and `$assume_pop` {#temp-assumptions}

These functions have signatures:
```civl
$system void $assume_push(_Bool pred);
$system void $assume_pop();
```
In the concrete semantics, `$assume_push` has the same semantics as `$assume`, and `$assume_pop` is a no-op.

In the symbolic semantics, they behave as follows: each process maintains a stack of boolean symbolic expressions known as the temporary assumption stack.
At any state, the *effective path condition* is the conjunction of the permanent path condition, and the conjunction,
over all processes *p*,  of all entries in the temporary assumption stack of *p*.
A call to `$assume_push` pushes a new assumption onto the temporary assumption stack of the process making the call.
A call to `$assume_pop` pops the stack of that process.
This allows prior assumptions to be removed.
This mechanism is consistent with the concrete semantics in that it yields an over-approximation of the set of reachable concrete states.
Specifically, removing a clause from the effective path condition can only increase the set of concrete states represented by a symbolic state.
This mechanism serves as a "widening operator" (in the sense of abstract interpretation), which enables symbolic execution to converge.

### Nondeterministic choice of integer: `$choose_int` {#choose_int}

This function has signature
```civl
$system int $choose_int(int n);
```
and returns an arbitrary integer in $[0, n - 1]$.  In verification mode, all possible choices are enumerated and explored. Note that $n$ is not required to be concrete. However, if it is not concrete CIVL may run forever in this case.

### Initialization by default value: `$default_value` {#default_value}

Assigns an object the default value of its type.  Signature:
```civl
$system void $default_value(void *ptr);
```
Assigns the default value to the object pointed to by `ptr`.
The default value is determined under the assumption that the object has static storage duration.
These default values are specified by the C standard for C types.
For example, 0 is the default value for a numeric type.

### Explicit elaboration of a domain: `$elaborate_domain` {#elaborate_domain}

Forces explicit enumeration of the elements of a finite [domain](#domain-type).  Signature
```civl
$system void $elaborate_domain($domain d);
```
In the concrete semantics, this is a no-op.

### Process exit: `$exit` {#exit}

Terminates the calling process.  Signature:
```civl
$system void $exit(void);
```

### Assignment of arbitrary value: `$havoc` {#havoc}

Assigns an arbitrary value of the object's type to an object.  Signature:
```civl
$system void $havoc(void *ptr);
```
In the concrete semantics, this function assigns an arbitrary value of the appropriate type to the object pointed to by `ptr`.

In the symbolic semantics, this function assigns a fresh symbolic constant of the appropriate type to the object pointed to by `ptr`.

### Hiding pointers: `$hide`, `$reveal`, and `$hidden` (*experimental*) {#hide-reveal-hidden}

The partial order reduction algorithm used by the CIVL Model Checker computes the set of memory locations that a process *p* can reach.
This is an analysis performed at a state.
The starting point of this analysis is the set of variables in the dynamic scopes referenced from *p* 's call stack, and the ancestors of those dyscopes.
There is an edge from one object to another if the first contains a pointer into the second.  All objects reachable from the initial objects in this directed
graph are the reachable objects of *p*.

There are times when one wants to modify this directed graph by ignoring some edge.
The purpose of these functions is to provide a way to do this from CIVL-C code.
The signatures are:
```civl
$abstract void* $hide(const void* ptr);
$system void* $reveal(const void* ptr);
$system _Bool $hidden(const void* ptr);
```
Function `$hide` wraps a pointer object in another object which is opaque to the reachability analyzer, i.e., the analyzer will not look inside
this object and therefore will not find the hidden pointer.
Nothing can be done with this hidden pointer until it is "revealed", i.e., extracted from the opaque object.
The function `$hidden` tells whether its argument is a value returned by `$hide`.

!!! note

    This is an experimental and generally unsound feature meant for developers. Use with caution.

### Checking pointer safety: `$is_derefable` {#is_derefable}

This function has signature
```civl
$system $state_f _Bool $is_derefable(void * ptr);
```
and determines if it is safe to evaluate (read) `*ptr`.
This means `ptr` points to a memory location that is capable of and is currently holding a value of type T, where T is the type of `*ptr`.

### Checking process termination: `$is_terminated` {#is_terminated}

Signature:
```civl
$system _Bool $is_terminated($proc p);
```
Determines whether `p` identifies a process that has terminated.
If `p` is undefined or `$proc_null`, returns 0.

### Specifying local regions: `$local_start` and `$local_end` {#local}

```civl
$system void $local_start();
$system void $local_end();
```

These primitives constrain the interleaving semantics of a program, similar to [the atomic statement](#atomic).
As with `$atomic`, `$local_start` obtains the atomic lock and/or increments the multiplicity;
`$local_end` decrements the multiplicity and/or releases the atomic lock.
At any state, if there is a process *t* that owns the atomic lock, only *t* is enabled.

The difference is as follows: when the atomic lock is free, if there is some process at a `$local_start` statement
and the first statement following `$local_start` is enabled,
then among such processes, the process with lowest PID is the only enabled process; that process executes `$local_start` and obtains the lock.
When *t* invokes `$local_end`, *t* decrements the atomic multiplicity, and if that multiplicity reaches 0, *t* relinquishes the atomic lock.
Intuitively, this specifies a block of code to be executed atomically by one process, and also declares that the block should be treated as a local statement,
in the sense that it is not necessary to explore all interleavings from the state where the local is enabled.

#### Example

The following illustrates the difference between local and atomic regions.
```civl
int x = 0;
void thread(int tid) {
  $atomic {
    x = tid;
  }
}
int main() {
  $parfor (int i:1..2) thread(i);
  $assert(x==2); // violated
}
```
The program above spawns two "threads", each of which writes to shared variable `x` in an atomic block.
The program has two possible executions: in one, thread 1 writes first, then thread 2 writes; in the other, thread 2 writes first, then thread 1 writes.
The assertion is violated on the second execution.
Running `civl verify` on this code will report a violation.

```civl
int x = 0;
void thread(int tid) {
  $local_start();
  x = tid;
  $local_end();
}
int main() {
  $parfor (int i:1..2) thread(i);
  $assert(x==2); // valid
}
```
In this program, the threads write to `x` within a local region.
This program has only one execution: first thread 1 executes the complete local region, then thread 2 executes the complete local region.
Running `civl verify` on this code yields "all properties hold".

#### Example

The code
```civl
void thread(int tid) {
  $local_start();
  ...A...
  $local_end();
  $local_start();
  ...B...
  $local_end()
}
int main() {
  $parfor (int i:1..2) thread(i);
}
```
also has only one execution.
Thread 1 will execute local block A and then release the atomic lock.
But at that point, thread 1 will again be the thread of lowest PID at a `$local_start` and therefore will be the only enabled thread.
It will execute local block B, and only then will thread 2 execute.
The code above is equivalent to
```civl
void thread(int tid) {
 $local_start();
  ...A...
  ...B...
  $local_end()
}
int main() {
  $parfor (int i:1..2) thread(i);
}
```

#### Use of `$yield` within a local region {#local-yield}

Local blocks can also be broken up at specified points using function `$yield`.
If *t* owns the atomic lock and calls `$yield`, then *t* relinquishes the lock and does not immediately return from the call.
When the atomic lock is free, there is no thread at a `$local_start`, a thread *t* is in a `$yield`, and the first statement following the `$yield` is enabled,
then *t* may return from the `$yield` call and re-obtain the atomic lock.

Note that a thread waiting to return from a `$yield` has no special priority, even if that `$yield` is inside at local region.
Only [$local_start](#local) grants a thread a special priority.

### Heap memory allocation: `$malloc` and `$free` {#malloc-free}

Allocate and deallocate heap memory.  Signatures:
```civl
$system void* $malloc($scope s, int size);
$system void $free(void *p);
```
Each dynamic scope has a heap.
Function `$malloc` allocates `size` bytes in the heap of scope `s`.
Unlike C's `malloc`, `$malloc` cannot fail.
Note that `malloc` is implemented as `$malloc($root, size)`.

Function `$free` deallocates the object pointed to by `p`, which should be a pointer that was returned by an earlier call to `$malloc`.
An error is generated if the pointer is not one that was returned by `$malloc`, or if it was already freed.

### Print the path condition: `$pathCondition` {#print-path-condition}

Prints the current effective path condition.
```civl
$system void $pathCondition(void);
```

### Power function: `$pow` {#pow}

Computes $x^{y}$.
```civl
$system double $pow(double base, double exp);
```

### Waiting for process termination: `$wait` and `$waitall` {#wait}

```civl
$system void $wait($proc p);
$system void $waitall($proc *procs, int numProcs);
```

Calling `$wait(p)` blocks the calling process until process `p` has terminated.
Calling `$waitall(procs, n)` blocks the calling process until all processes `procs[0]`, ...,`procs[n-1]` have terminated.
All the process arguments must refer to valid processes, i.e., they cannot be undefined or `$proc_null`.
It is OK if a process argument refers to a process that has already terminated.

### Yielding atomicity: `$yield` {#yield}

```civl
$system void $yield();
```

The yield function is used inside an [atomic](#atomic) or [local](#local) region to release the atomic lock so that other processes may execute.
The yielding process retains its current atomic lock multiplicity, so that if and when it regains the atomic lock, it proceeds executing
with the same multiplicity it had before the yield.
In order for the yielding process to return from the call to `$yield`, and thereby regain the atomic lock, the first statement following
`$yield` must be enabled.
This statement behaves as a guard for regaining the atomic lock, just as the first statement of an atomic block, or the first statement
following `$local_start`, acts as a guard for entering an atomic or local region.
The yielding process competes with other processes to obtain the lock; it does not have any special priority.
In particular, if the lock is available and there is a process at a `$local_start` call (and the first statement following the call is enabled), one such a process is guaranteed to obtain the lock.

## Macros {#macros}

### Elaboration of integer value: `$elaborate` {#elaborate}

This is a function-like macro of one argument.
The argument should be a side-effect-free expression of integer type.
The macro expands to text which, when followed by `;`, yields a statement.
The definition is:
```civl
#define $elaborate(x)  for(int _i = 0; _i < (x); _i++)
```
In the concrete semantics, this is no-op.
However, in the symbolic semantics, it has the effect of forcing $x$ to take on a concrete value.
Let us assume $x \ge 0$ is implied by the path condition.
Then, each time the process completes one iteration of this loop, a nondeterministic choice is made to decide $n < x$, where  $n$ is the current concrete value of `_i`.
If the true branch is taken, then $x > n$ is recorded in the path condition and control moves to the next iteration.
If the false branch is taken, then $x = n$ is recorded and control exits the loop with $x$ now assigned a concrete value.
Hence this effectively enumerates all possible concrete values of $x$.
As long as the path condition implies some concrete upper bound on $x$, this will be a finite enumeration.
If there is no such bound, the symbolic state space will be infinite, since the loop can iterate indefinitely.

## Contract Annotations {#contracts}

Function contracts can be specified as annotations---formatted comments---preceding a function prototype or definition.
The language for these annotations is based on the specification language [ACSL](https://frama-c.com/html/acsl.html) used by Frama-C.
A translation unit must include the following pragma to inform CIVL that it should parse the ACSL annotations in that translation unit:
```c
#pragma CIVL ACSL
```
If this line is not present, the annotations will be ignored.

The high-level syntax for a procedure contract is

> `/*@` *clause*\* `*/`

Optionally, any line after the first line (which begins with `/*@`) in the contract may have `*` as its first non-white-space character;
this character is ignored.

There are clauses for preconditions, postconditions, and other concepts.
These are used in various experimental extensions of CIVL.
Here we describe only the clauses which are fully supported and used by the core CIVL model checker.

### Specifying a guard: `executes_when`

This clause has syntax

> `executes_when` *expr* `;`

It may be used with a [system function](#system).
(For all other functions, this clause is ignored.)
This clause specifies a guard that applies whenever the function is called.
The *expr* is an ACSL expression and may use the formal parameters of the function as well as any other variables that are in scope.
Whenever control in a process is at a call to the function, the call will be enabled only if the guard evaluates to *true*.
To evaluate the guard, the actual arguments used in the call are substituted for the formal parameters in the guard expression.

Note that `\true` should be used for the Boolean constant *true* in the ACSL expression.   E.g.,
```civl
\*@ executes_when \true; */
$system void f(int x);
```

!!! note

    if the actual arguments have side-effects, the code is automatically refactored so that the side-effects occur once, before the call.
    I.e., the call

    > *f*`(`*expr1*`,` *expr2*`,` ...`)`

    is semantically equivalent to
    > `t1 =` *expr1*`;`
    >
    > `t2 =` *expr2*`;`
    >
    > ...
    >
    > *f*`(t1, t2,` ... `)`

Hence the guard may be evaluated any number of times without causing additional side-effects.

### Specifying dependencies: `depends_on` {#depends_on}

A depends_on clause may be used with a system or atomic function and has the form

> `depends_on` ( `\nothing` | *access-list* ) `;`

where *access-list* has the form
> `\access` `(` *expr* ( `,` *expr* )\* `)`

Each expression in the access list shall have a pointer type.

The clause specifies the dependency relation for the system or atomic function.
This dependency information is used by the model checker's partial order reduction algorithm to restrict the interleavings explored.

The precise semantics: for a process that is at a state from which a call to the function is enabled:
any transition from another process that does not access any object pointed to by an expression in the access list
will be independent of the function call.

(Two transitions are independent if, from any state in which both are enabled, neither can disable the other, and the two transitions commute,
i.e., executing the fist and then the second results in the same state as executing the second and then the first.)

This clause should be used with care: it is possible to specify a depends-on clause which is not sound, i.e.,
the clause may declare that two transitions are independent when they are not.
In this case, the model checker may skip some interleaving on which a violation occurs, and report that a program is correct when it is not.
This mechanism is intended for use by expert library developers.
