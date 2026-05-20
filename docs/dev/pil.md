# PIL: Parallel Intermediate Language

## Basic Properties

1. PIL programs are target of translators from C, Fortran, MPI/OpenMP/etc. They can also be written by humans.
1. PIL should be a high-level language like CIVL-C.
1. PIL programs can be easily translated to PFG.
1. PIL must also have a totally well-defined semantics and syntax.
1. Every operation should have a well-defined outcome, even division by zero or an illegal pointer dereference.  However, when analyzing a PIL program, a user can specify exactly which of these operations should be considered erroneous and reported.   A reasonable default might be to make all of them erroneous.
1. PIL programs can have nested funtion definitions.
1. PIL programs can use preprocessor directives like in C
1. There are no automatic conversions.  There is no "array-pointer pun".  All conversions must be done by explicit casts or other expressions.  To convert an array `a` to a pointer to element 0 of `a`, write `&a[0]`.
1. PIL supports `$input`/`$output` variables in global scope (like CIVL-C)
1. PIL identifiers are the same as C's, e.g., `x`, `f10`, ...
1. Keywords, built-in or library functions, constants, and types not in C start with `$` (like CIVL-C)
1. PIL supports libraries: similar to C, `#include <stdlib.h>`, but these name PIL libraries and there are additional standard PIL libraries not in the C standard library
1. Program order:
    1. A program is a set of variable, function, and type definitions.
    1. The order is totally irrelevant. [NOTE: except for initializers?]
    1. A variable can be used anywhere it is in scope.
    1. A function can be called anywhere it is in scope.
1. PIL programs can be divided into multiple files (translation units)
    1. One TU can refer to a variable, function, or type, in another TU.
    1. The variable just needs to be declared somewhere in the TU.
    1. The function just needs a prototype somewhere in the TU.
    1. The use of `static` in a variable declaration or function definition makes it private to that TU (so two can have same name), as in C.
    1. There is no need for `extern` so this is not in PIL.
    1. At most one of the TUs declaring the variable can have an initializer.
    1. At most one of the TUs can have a definition for a function.

## Types

### Basic properties of types

**type names** are used for all declarations.  There are no C declarators.  Examples:

* `$int[] a`: declares `a` to be an array of integer
* `$int* p`: pointer to integer
* `$int*[] b`: array of pointer to integer
* `$int[]* q`: pointer to array of integer
* `$int*[]($real) f`: function from Real to array of pointer to integer

Type definitions have the form: `typedef typename ID;`.

Every type has a default value.  The result of evaluating an erroneous expression is the default value of that type.  (But as explained above, an analyzer may check for all errors.)

### The types

* basic types:
    * `$bool` : the set consisting of `$true` and `$false`.  Default value: `$false`.
    * `$int`: the set of integers.  Default value: 0.
    * `$real`: the set of Reals.  Default value: 0.0.
    * `$int<N>`: set of integers [-N, N-1]. `N` is a constant positive integer expression so is statically known.  Underflows or overflows are erroneous.  Default value: 0.
    * `$uint<N>`: set of integers [0,N-1].  `N` is a constant positive integer expression so is statically known.  Arithmetic is performed modulo `N` (like C's unsigned integer types).   Default value: 0.
    * `$float<p,emax>`: the set of IEEE binary floating point numbers with precision `p` and emax `emax`.  Both `p` and `emax` are constant integer expressions.  Default value: 0.0.
* `T*`: pointer to `T`.  Default value: `NULL`.
* `struct TAG { T1 f1; ... Tn fn; }`: a C struct.  Default value: the struct value in which each field has its default value.
* `union`: a C union.   Default value: the union value in which the first field has its default value.
* `T[]`: sequence of `T`.  Note: there is no "T[n]".  Sequences are first-class values: they can be assigned, returned, passed as arguments, etc.  Default value: the sequence of length 0.
* `R(T1, ..., Tn)`: the type of a procedure which consumes values in `T1`, ..., `Tn` and returns a value in `R`.   This is basically C's function type.  Default value: the procedure which does nothing (has no side-effects) and returns the default value of `R` for any inputs.
* `$fun<T1,T2>` :  "logic functions":  deterministic, total, side-effect free functions from `T1` to `T2`.  Note however the function may depend on the state (i.e., the state should be considered a hidden input).   A `$pure` function is a function that does not depend on the state.  Default value: the function that returns the default value of `T2` on any input.
* `$set<T>` : finite set of `T`.  Default value: the empty set.
* `$map<T1,T2>` : finite map from `T1` to `T2`.  A map is a set of ordered pairs `(x,y)` with the property that if `(x,y)` and `(x,z)` are in the map, then `y`=`z`.  Default value: the empty map.
* `$tuple<T1,...>` : tuples of specified type.  This is similar to `struct`, but there is no tag and the fields do not have names.  Default value: the tuple in which each component has its default value.
* `$obj` : the object type.   This is a type for representing a sequence of bytes holding arbitrary values at different intervals.   It is used to model C objects, such as the object created by a call to C's `malloc`.

### Sized types

The types above are the *unsized types*  A *sized type* comprises an unsized type and a positive integer, which represents the size of the type in bytes.  Syntactically, a sized type is specified

```
  $size(expr) typename
```

where expr is an integer constant expression and typename is the name of an unsized type.  For example,

```
typedef $size(1) $int char;
typedef $size(4) $int int;
typedef $size(8) $int long;
typedef $size(4) $real float;
typedef $size(8) $real double;
```

defines 5 distinct types.  Certain operations require a sized type.

Restrictions on sized types:

* an array type cannot be sized
* the type `$obj` cannot be sized
* if a struct type is sized and all of its fields have sized types then the size of the struct
must be the sum of the sizes of the fields
* if a union type is sized then the size of the union must be greater than or equal to the
size of any sized type field

Note on translation from C: C structs can have "padding" after each field.  This can be modeled
in PIL by making the padding explicit, e.g.,
```
struct Pair {
  int i;
  char[2] _padding1;
  double x;
  char[4] _padding2;
```

Except... you can do things like `char[2]` in PIL, hmmm, have to think about this.


## Functions

There are two kinds of functions in PIL:

1. Procedural functions = "procedures".  A procedure has a type of the form `R(T1,...,Tn)` where `R` is the return type and the `Ti` are the input types.
1. Logic functions.  One of these has a type of the form `$fun<R,T1,...,Tn>`.

### Procedures

A procedure is similar to a C function.  It consumes some values of a specified type and possibly returns a value of a specified type.  Procedure definitions look like C function definitions:
```
  R f(T1 x1, ..., Tn xn) { stmts }
```
defines a procedure named `f` which consumes inputs of types `T1`, ..., `Tn` and returns a value of type `R`.    `R` can be `void` if the procedure does not return a value.

The definition above defines a **constant** `f` of type **`R(T1, ..., Tn)`**.   Procedures are first-class values.    One may declare a variable of type `R(T1, ..., Tn)`, a procedure may return a value of that type, a procedure may consume a value of that type, a value of that type may be assigned to a variable, etc.   Hence the procedure type is just like any other type, and procedure definitions define new constants of that type, just as `1` is a constant of type `$int`.   Note this is different from C in that C uses function pointers; PIL dispenses with the need for function pointers and just uses functions.

A **procedure call expression**  has the usual form `g(e1, ..., en)`.  This is an expression that can be used anywhere an expression with side-effects is allowed.  Here, `g` is an expression of functional type, say `R(T1, ..., Tn)`, and `ei` is an expression of type `Ti` (for i=1, ..., n).  The procedure call expression has type `R`.

Procedure calls can have side-effects, be nondeterministic, and the behavior can depend on non-local state; they may access any variable in scope, the statements may dereference pointers, etc.

```
  int f(int x) { return x+1; }  // f is a constant of type int(int)
  int callon1( (int)int g ) {
    return g(1);
  }
  ...
  int y = callon1(f); // y is now 2
```

Procedure definitions can be nested.   It is an error to call a procedure `f` when `f` is not in scope.  (This is similar to GNU C.)   In other words, if the call takes place in dyscope d, then the definition of `f` must be in d's static scope, or in the parent of that scope, or its parent, etc.

There is a second way to specify a procedure, using a lambda expression, which is described below.

### Logic functions

Logic functions are a certain class of functions that have no side-effects, and are deterministic total functions of their arguments and the current state.  A logic function has a type of the form `$fun<R,T1,...,Tn>`, which signifies the set of logical functions which consume inputs of type `T1`, ..., `Tn` and return a value of type `R`.

Logic functions are also first-class objects in PIL.   An application of a logic function `f(x1,... ,xn)` is a side-effect-free expression that can be used anywhere an expression is allowed.  A logic function is not necessarily pure, i.e., the value of an application may depend on any part of the state, not just the arguments.

Despite the apparent similarity with procedures, logic functions and procedures are clearly distinguished and one cannot be converted to another.

A logic function can be defined as follows:
```
  $logic R f(T1 x1, ..., Tn xn) = expr;
```
where `expr` is a side-effect-free expression of type `R` and can refer to any variables in scope.

### Misc.

Both procedure and logic function definitions can be templated, e.g.,
```
       <T1,T2> int f($map<T1,T2> f, T1 x) { ... }
       <T1,T2> $logic int g($map<T1,T2> f, T1 x) { ... }
```
This defines one procedure or logic function for each assignment of types to the `Ti`.

Both kinds of functions can be declared without providing definitions, indicating that the definition can be found in a different translation unit:
```
  int f(int x);
  $logic int g(int x);
```

### Lambda expressions

Lambda expressions can be used to define functions that are anonymous and that are **closures**, i.e., have an associated environment that persists for the life of the function.

A lambda expression that specifies a procedure closure has the form:
```
  $lambda [U1 v1=init1; ... Um vm=initm;] R (T1 x1, ..., Tn xn) { stmts }
```
where

* the `Ti` and `Uj` are types
* the `xi` and `vj` are variables
* the `initj` are expressions that can refer to any variables in scope
* R is a type (the return type), which may be void
* {S1; ...} is a block (same as in a procedure definition)
* if R is not void, the block must return a value of type R
* the only variables that can occur free in the block are the `xi` and `vj`.

The type of this expression is `R(T1, ..., Tn)`.   The resulting value is a procedure which can be called or assigned to a variable, etc., just like any other procedure value.

Note that the definition can only use the specified variables.  Evaluating this expression yields a closure, which is a pair consisting of a dyscope and the body of the procedure.   The dyscope has variables `vj`, which are initialized by evaluating the `initj` when the lambda expression is evaluated.  The body of the procedure may read and write to the `vj`.  That dyscope has no parent and will live as long as the procedure is around.  Hence a function may return a closure and that closure may still be called at any time, anywhere in the program, regardless of whether the original lambda expression is in scope.

When a procedure closure is called, a new dyscope is created whose parent dyscope is the dyscope of the closure.  In the new dyscope, the formal parameters are assigned the actual values and procedure is executed in that scope.  When it returns, the new dyscope is removed.

A lambda expression that specifies a logic function has the form
```
  $lambda [U1 v1=init1; ... Um vm=initm;] R (T1 x1, ..., Tn xn) expr
```
where

* the `Ti` and `Uj` are types
* the `xi` and `vj` are variables
* the `initj` are expressions that can refer to any variables in scope
* R is a type (the return type), which cannot be void
* `expr` is a side-effect-free expression of type R
* the only variables that can occur free in `expr` are the `xi` and `vj`.

The type of this expression is `$fun<R, T1, ..., Tn>`.   As with procedural lambdas, this yields a logic function with a dynamic scope that persists, so can be called anywhere, even after the lambda expression goes out of scope.

## Tuples

Expressions: these are all side-effect-free, assuming `expr1`, ... are side-effect-free.

* `t1 == t2`
    * the two tuples have the same type and corresponding components are equal
* `t.(i)`
    * the value of component `i` (an integer constant) of tuple `t`.  Syntax error if `i` is out of range for the tuple type.
* `($tuple<T1,...>){ expr1, ... }`
    * the literal tuple with the given list of components.
* `t[.(i) := expr1]`
    * the tuple which is the same as `t`, except for component `i` (an integer constant), which has value `expr1`.  Syntax error if `i` is out of range.

Mutating expressions:

* `t.(i) = expr` : sets component `i` of tuple `t` to `expr`.  Here `i` is an integer constant; it is a syntax error if `i` is out of range.

## Sets

Side-effect free expressions:

* `s1 == s2`
    * the two sets have the same type and contain the same elements
* `($set<T>)$empty`
    * empty set of type T

Logic functions:

* `$pure $logic $bool $set_contains($set<T> s, T x);`
    * is x an element of s?
* `$pure $logic $set<T> $set_with($set<T> s, T x);`
    * s U {x}
* `$pure $logic $set<T> $set_without($set<T> s, T x);`
    * s - {x}
* `$pure $logic $set<T> $set_union($set<T> s1, $set<T> s2);`
    * s1 U s2
* `$pure $logic $set<T> $set_difference($set<T> s1, $set<T> s2);`
    * s1-s2
* `$pure $logic $set_intersection($set<T> s1, $set<T> s2);`
    * s1  \cap s2
* `$pure $logic T[] $set_elements($set<T> s);`
    * returns the set of elements of s as an array in some deterministic order
* `$pure $logic $bool $set_subset($set<T> s1, $set<T> s2);`
    * is s1 a subset of s2?
* `$pure $logic $set<U> $set_map($set<T> s, $fun<T,U> f);`
    * { f(x) | x in s}
* `$pure logic $set<T> $set_comprehension($set<T>, $fun<T,$bool> p);`
    * { x in s | p(x) }

Mutating procedures:

* `$bool $set_add($set<T> * this, T x);`
    * adds `x` to `this`.  Returns `$true` iff `x` was not in `this`, i.e., if the operation results in a change to the set.
* `$bool $set_remove($set<T> * this, T x);`
    * removes `x` from `this`.  Returns `$true` iff `x` was in `this`, i.e., if the operation results in a change to the set.
* `void $set_addAll($set<T> * this, $set<T> that);`
    * adds all of the elements of `that` to `this`
* `void $set_removeAll($set<T> * this, $set<T> that);`
    * removes all of the elements of `that` from `this`
* `void $set_keepOnly($set<T> * this, $set<T> that);`
    * removes any elements of `this` which is not in `that` from `this`

## Sequences (arrays)

Expressions:

* `a1 == a2`
    * the two sequences have the same type and length and corresponding elements are equal
* `a[i]`
    * the `i`-th element of `a`. Here `a` is an expression of type `T[]` and `i` is an expression of type `$int`.  Requires `i` to be in range, i.e., `i` is in [0,n-1], where n is the length of `a`.   (Otherwise the expression is erroneous.)
* `a[i:=x]`
    * the sequence which is the same as `a`, except in position `i`, where it has value `x`.  Here `a` is an expression of type `T[]`, `i` is an expression of type `$int`, and `x` is an expression of type `T`.  Requires `i` to be in range for `a`.
* `(T[]){ expr1, ... }`
    * the sequence obtained by evaluating the given expressions, each of which has type `T`

Logic functions:

* `$pure $logic T[] $seq_fun($int len, $fun<$int,T> f);`
    * the sequence of length `len` whose value at position `i` is `f(i)`
* `$pure $logic T[] $seq_uniform($int n, T val);`
    * the sequence of length `n` in which every component is `val`
* `$pure $logic $int $length(T[] a);`
    * the length of `a`
* `$pure $logic T[] $seq_subseq(T[] a, int i, int n);`
    * the sequence of length n,  a[i..i+n-1].  Requires that i..i+n-1 are all in range for `a`
* $pure $logic T[] $seq_with(T[] a, int i, T x);`
    * the sequence obtained from `a` by inserting `x` at position `i` and shifting the subsequent elements up.  Requires `i` to be in [0,n], where `n` is the length of `a`.
* `$pure $logic T[] $seq_without(T[] a, int i);`.  Requires `i` to be in [0,n-1], where `n` is the length of `a`.
    * the sequence obtained from `a` by deleting the element in position `i` and shifting subsequent elements down
* `$pure $logic T[] $seq_concat(T[] a1, T[] a2);`
    * the sequence obtained by concatenating the two given ones
* `$pure $logic U[] $seq_map(T[] a, $fun<T,U> f);`
    * the sequence obtained by applying `f` to every element of `a`
* `$pure $logic T[] $seq_filter(T[] a, $fun<T,$bool> f);`
    * the sequence obtained from `a` by removing every element for which `f(a[i])` is `$false`
* `$pure $logic U $seq_foldl(T[] a, $fun<$tuple<T,U>,U> f, U init);`
    * the sequence b which has the same length as `a`, and where `b[0]`=`f(init, a[0])`, `b1`=`f(b[0], a[1])`, `b2=f(b[1], a[2])`, etc.
* `$pure $logic U $seq_foldr(T[] a, $fun<$tuple<T,U>,U> f, U init);`
    * the sequence b which has the same length as `a`, and where `b[n-1]`=`f(a[n-1], init)`, `b[n-2]`=`f(a[n-2], b[n-1])`, etc.

Mutating expressions:

* `a[i]=x;`
    * assigns value of `x` to `a[i]` and returns value of `x`.   Here `a` has array type, say `T[]`, `x` is an expression of type `T`, and `i` is an expression of type `$int`

Mutating procedures:

* `T $seq_remove(T[] * this, int i);`
* `void $seq_insert(T[] * this, int i, T x);`
* `void $seq_append(T[] * this, T[] that);`

## Maps

Side-effect-free expressions:

* `m1 == m2`
    * the two maps have the same type and the same entries
* `($map<K,V>)$empty`
    * the empty map from `K` to `V`

Logic functions:

* `$pure $logic V $map_get($map<K,V> K key);`
* `$pure $logic $bool $map_containsKey($map<K,V> map, K key);`
* `$pure $logic $bool $map_containsValue($map<K,V> map, V val);`
* `$pure $logic $map<K,V> $map_with($map<K,V> map, K key, V val);`
* `$pure $logic $map<K,V> $map_without($map<K,V> map, K key);`
* `$pure $logic $set<K> $map_keys($map<K,V> map);`
* `$pure $logic $set<$tuple<K,V>> $map_entries($map<K,V> map);`

Mutating procedures:

* `V $map_put($map<K,V> * this, K key, V val);`
* `V $map_remove($map<K,V> * this, K key);`
* `void $map_removeAll($map<K,V> * this, $set<K> keys);`
* `void $map_addAll($map<K,V> * this, $map<K,V> that);`

## Heaps

There is a single heap for dynamic allocation.  The following built-in procedures are provided:

* `T* $new<T>()`
    * creates an object `A` of type `T` in heap and returns `&A`.
* `T* $alloc<T>(int n)`
    * creates an array `A` of length `n` of `T` in heap and returns `&A[0]`.
* `void $free<T>(T* p)`
    * frees the object referred to by `p`, the pointer returned by an earlier call to `$new` or `$alloc`.

## Objects

* `$obj $empty_obj($int size);`
    * this function returns the empty object spanning `size` bytes. Only values of a sized type can be written to an object.
* `void* $obj_base($obj* optr);`
    * returns a pointer to byte 0 of the object.  That pointer can be cast to different pointer types in order to access the object.

An object is modified through a pointer into the object, for example:
```
typedef $size(4) $int int;
typedef $size(8) $real double;
$obj o1 = $empty_obj(1000);
double* dp = (double*)$obj_base(&o1);  // points to byte 0
*dp = 3.14;
*(dp+7) = 2.718;
int* ip = (int*)dp;
*(ip+2) = 17;
```
The operations above place the double value 3.14 in `o1` at positions 0..7, the double value 2.718 at positions 28..35, and the integer value 17 at positions 8..11.

If a write to an object overlaps the interval of an existing value, the old value is removed.

The values in an object are also read using pointers.



## Questions

* how to deal with "undefined" values?
* can there be nondeterministic expressions, i.e., expressions that evaluate to a set of values instead of one value?
* can there be nondeterministic statements, i.e., statements which result in multiple transitions
* how to implement C's malloc?

