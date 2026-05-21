# CIVL Libraries

In progress.

## bundle

Header: [bundle.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/bundle.cvh).
Uses: [op](#op).

A bundle is a sequence of objects of the same type, wrapped into an atomic package.
A bundle is created using a function that specifies a region of memory.
One can create a bundle from an array of integers, and another bundle from an array of reals.
Both bundles have the same type, `$bundle`.
They can therefore be entered into an array of `$bundle`, for example.
Hence bundles are useful for mixing objects of different (even statically unknown) types into a single data structure.
Later, the contents of a bundle can be extracted with another function that specifies a region of memory into which to unpack the bundle;
if that memory does not have the right type to receive the contents of the bundle, a runtime error occurs.

### Types
* `$bundle`: the bundle type

### Functions
* `$system int $bundle_size($bundle b)`
    * Returns the size of the given bundle, i.e., the number of bytes consumed by all elements of the bundle.  This is the product of the number of elements and the size of one element.
* `$system $bundle $bundle_pack(void *ptr, int size)`
    * This function creates a bundle from the memory region specified by `ptr` and `size` (the total number of bytes to copy), copying the data into the new bundle. It returns the new bundle.
* `$system  void $bundle_unpack($bundle b, void *ptr)`
    * This function copies the data from the given bundle into the memory region starting at `ptr`.  The memory region extends for `$bundle_size(b)` bytes.
* `$atomic_f void $bundle_unpack_apply($bundle data, void *buf, $operation op, int count, void *result)`
    * This function unpacks the bundle while applying the specified numeric operation. Parameter `op` specifies a binary operation.  For each *i* in 0..*count*-1, the operation is applied to the *i*-th element of `buf` and the *i*-th element of the bundle, and the result is stored in the *i*-th position of `result`.  Parameter `count` is the number of elements in the bundle; `buf` and `result` should each point to a region of memory capable of holding at least `count` elements.

### Example
```civl
#include <bundle.cvh>
int main() {
  int n = 10;
  int a[n], b[n], c[n];
  for (int i=0; i<n; i++) a[i] = i;
  $bundle bun = $bundle_pack(a, n*sizeof(int));
  $assert($bundle_size(bun) == n*sizeof(int));
  $bundle_unpack(bun, b);
  $assert($forall (int i: 0..n-1) b[i]==a[i]);
  $bundle_unpack_apply(bun, b, _SUM, n, c);
  $assert($forall (int i: 0..n-1) c[i]==2*a[i]);
}
```

## comm {#comm}

Header: [comm.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/comm.cvh).
Uses: [bundle](#bundle).

The comm library supports message-passing-style communication.
It provides a number of basic data structures which can be used to implement specific message-passing interfaces, such as MPI.

### Types
* `$message`: A message consists of data and meta-data.  The data is a finite sequence of objects, all of the same type.  The meta-data consists of 3 integers: the source, destination, and tag.   The source identifies the process that sent the message.  The destination identifies the process to which the message is sent.   Both `source` and `destination` are "places", which are nonnegative integer IDs that abstract places from which messages are sent or received.   Typically, a single place is associated to each process in a message-passing system, but this is not required.  The tag is an additional nonnegative integer that can be used to represent anything the user wants; the receiving process may select a message based on the tag.
* `$gcomm`: This is an opaque handle to an object that stores the state of the message-passing system.  In particular, the buffered messages (those that have been sent, but not yet received) are stored in the gcomm.
* `$comm`:  Each place has its own comm, an opaque handle local to that place that is used to access all message-passing functions (e.g., sends and receives) at that place.  It contains a reference to the gcomm.

### Constants
* `$COMM_ANY_SOURCE`: a constant that can be used as the source argument in a dequeue operation to indicate that a message may be taken from any source
* `$COMM_ANY_TAG`: a constant that can be used as the tag argument in a dequeue operation to indicate that a message with any tag may be selected
* `$comm_null`: a constant representing the absence of `$comm`

### Functions
* `$atomic_f $message $message_pack(int source, int dest, int tag, const void *data, int size)`
    * Creates a new message with the given meta-data, and the data taken from the memory region that begins at `data` and extends for `size` bytes.  The data is copied into the new message object.  The source, destination, and tag must be nonnegative.  The size may be 0.
* `$atomic_f int $message_source($message message)`
    * Gets the source of the message.
* `$atomic_f int $message_tag($message message)`
    * Gets the tag of the message.
* `$atomic_f int $message_dest($message message)`
    * Gets the destination of the message.
* `$atomic_f int $message_size($message message)`
    * Gets the size of the message data (in bytes).
* `$atomic_f void $message_unpack($message message, void *buf, int size)`
    * Extracts the data from a message, storing it in the buffer that starts at `buf`.  The argument `size` must be greater than or equal to the size of message, else an error occurs.
* `$atomic_f $gcomm $gcomm_create($scope scope, int size)`
    * Allocates a new gcomm object that will be stored in the heap of the specified scope.  The `size` is the number of "places" that will be associated to this gcomm.  The place IDs range from 0 to `size`-1 (inclusive).
* `$atomic_f int $gcomm_destroy($gcomm gcomm, void * junkMsgs)`
    * Destroys the gcomm, deallocating memory that was allocated for it.  If `junkMsgs` is not NULL it shall be a pointer to an array of indeterminate length of `$message`.   This function will populate that array with all messages remaining the gcomm's buffers and return the number of such messages.
* `$atomic_f void $gcomm_dup($comm comm, $comm newcomm)`
    * Duplicates the communicator `comm`.   The `newcomm` is initialized to have the same gcomm and place as the original `comm`.
* `$atomic_f $comm $comm_create($scope scope, $gcomm gcomm, int place)`
    * Creates a new `comm` associated with the specified `place` in `gcomm`.  If `place` is less than 0, or greater than the size of `gcomm`, or another process has already occupied `place` in `gcomm`, an error occurs.  The new comm object is allocated in the heap of the given scope.
* `$atomic_f void $comm_destroy($comm comm)`
    * Deallocates the comm.
* `$atomic_f int $comm_size($comm comm)`
    * Gets the size of the gcomm that owns `comm`.
* `$atomic_f int $comm_place($comm comm)`
    * Gets the place of the given `comm` in its gcomm object.
* `$system void $comm_enqueue($comm comm, $message message)`
    * Enqueues a message in a FIFO channel in the gcomm.  The message should have its source be the place of `comm`.   The message will enqueued on channel *(i,j)* in the gcomm, where *i* is the place of the source, and *j* is the place of the destination of the message.
* `$system $state_f _Bool $comm_probe($comm comm, int source, int tag)`
    * Determines whether there exists a message with the given source and tag, and with destination the place of `comm`, in the gcomm associated to `comm`.   `source` may be `$COMM_ANY_SOURCE` and `tag` may be `$COMM_ANY_TAG`.
* `$system $message $comm_seek($comm comm, int source, int tag)`
    * Returns the oldest message in the gcomm associated to `comm` that matches `source` and `tag` without modifying gcomm. `source` may be `$COMM_ANY_SOURCE` and `tag` may be `$COMM_ANY_TAG`. If no such message found, returns a message with empty data and all meta-data being `-1`.
* `$system $message $comm_dequeue($comm comm, int source, int tag)`
    * Dequeues a message in a FIFO channel in the gcomm. The message should have its destination be the place of `comm`. The message will be dequeued from channel *(i,j)* in the gcomm, where *i* is the place of the source, and *j* is the place of the destination of the message.  A call to this function will not return until a matching message can be dequeued.
* `$system $state_f _Bool $comm_empty_in($comm )`
    * deprecated
* `$system $state_f _Bool $comm_empty_out($comm )`
    * deprecated



## concurrency {#concurrency}

Header: [concurrency.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/concurrency.cvh).
Uses: [bundle](#bundle).

### Types
* `$gbarrier`
    *  A data type representing a handle to a global barrier object.
* `$barrier`
    * A data type representing a process-local handle used to operate a global barrier

### Functions
* `$atomic_f $gbarrier $gbarrier_create($scope scope, int size)`
    * Creates a new global barrier object and returns a handle to it.  The barrier has the specified `size`. The new object will be allocated in the given `scope`.
* `$atomic_f void $gbarrier_destroy($gbarrier gbarrier)`
    * Destroys the global barrier object referred by the handle `gbarrier`
* `int $get_nprocs($gbarrier gbarrier)`
    * Returns the size of the global barrier object
* `$atomic_f $barrier $barrier_create($scope scope, $gbarrier gbarrier, int place)`
    * Creates a process-local handle for operating the global barrier referred by `gbarrier`.  The `place` must be in [0,`get_nprocs(gbarrier)`-1].  It specifies the place in the global barrier that will be occupied by the local handle.
* `$atomic_f void $barrier_destroy($barrier barrier)`
    * Destroys the local handle to a global barrier
* `void $barrier_call($barrier barrier)`
    * Calls the barrier referred by `barrier`
* `void $barrier_call_yield($barrier barrier)`
    * Calls the barrier from within an atomic section: this will yield at the point where process is waiting to exit barrier.
* `void $barrier_call_subset($barrier barrier, int nprocs)`
    * under construction
* `void $barrier_call_execute($barrier barrier, void foo(void))`
    * under construction

## domain {#domain}

Header: [domain.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/domain.cvh).
Uses: (nothing).

### Types
* `$domain_strategy`
    * An enumeration type used to specify how domains are decomposed.  There are three options: `ALL`, `RANDOM`, and `ROUND_ROBIN`.
* `$domain_decomposition`
    * A data structure representing the decomposition result of a domain.

### Functions
* `$system $domain_decomposition $domain_partition($domain domain, $domain_strategy strategy, int n)`
    * Takes a domain and some n>0 and returns some partition of the domain into n sub-domains, according to the decomposition strategy specified.
* `$system $range $range_of_rectangular_domain($domain dom, int index)`
    * Returns the `index`-th range of the given domain `dom`. Requires `index` to be between 1 and the dimension of `dom` minus 1.
* `$system int $high_of_regular_range($range range)`
    * Returns the upper bound of a regular range.
* `$system int $low_of_regular_range($range range)`
    * Returns the lower bound of a regular range.
* `$system int $step_of_regular_range($range range)`
    * Returns the step of a regular range
* `$system _Bool $is_rectangular_domain($domain domain)`
    * Returns true if this `domain` is a rectangular domain.
* `$system _Bool $is_regular_range($range range)`
    * Returns true if this `range` is a regular range, i.e., it is composed of a lower bound, a higher bound and a step.
* `$system int $dimension_of($domain domain)`
    * Returns the dimension of the given domain.

## mem {#mem}

Header: [mem.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/mem.cvh).
Uses: (nothing).

The `mem` library provides functions to manipulate objects of the CIVL-C built-in type---`$mem`.  A `$mem` object represents a set of memory locations.

### Functions

* `$system void $write_set_push()`
    * Pushes a new entry (i.e. an empty `$mem`) onto the write stack of the calling process.  Each process maintains a write stack. A stack entry is a `$mem` object. A write to a memory location will cause the memory location being added to the top entry of the write stack, if the stack is non-empty.
* `$system $mem $write_set_pop()`
    * Pops and returns the top entry of the write stack of the calling process.
* `$state_f $system $mem $write_set_peek()`
    * Returns the top entry of the write stack of the calling process.
* `$system void $read_set_push()`
    * Pushes a new entry (i.e. an empty `$mem`) onto the read stack of the calling process.  Each process maintains a read stack. A stack entry is a `$mem` object.  Reading a memory location will cause the memory location being added to the top entry of the read stack, if the stack is non-empty.
* `$system $mem $read_set_pop()`
    * Pops and returns the top entry of the read stack of the calling process.
* `$state_f $system $mem $read_set_peek()`
    * Returns the top entry of the read stack of the calling process.
* `$atomic_f $system $mem $mem_empty()`
    * Creates a new empty `$mem` object.
* `$atomic_f $system _Bool $mem_equals($mem m0, $mem m1)`
    * Returns true iff `m0` and `m1` are the identical set of memory locations.
* `$atomic_f $system _Bool $mem_contains($mem super, $mem sub)`
    * Returns true iff `super` is a super-set of `sub`.
* `$atomic_f $system _Bool $mem_no_intersect($mem m0, $mem m1, $mem *out0, $mem *out1)`
    * Returns true iff `m0` and `m1` are two disjoint sets. If `m0` and `m1` overlap, `out0` and `out1` will be assigned two overlapping memory location sets, which are as small as the function can figure out from `m0` and `m1`, respectively.
* `$atomic_f $system $mem $mem_union($mem mem0, $mem mem1)`
    * Returns the union of the two sets of memory locations `mem0` and `mem1`.
* `$atomic_f $system $mem $mem_union_widening($mem, $mem)`
    * Similar to the `$mem_union` function but may return an over-approximated super-set of the union set.  The over-approximation is done by the model checker in order to make state space finite.
* `$atomic_f $system void $mem_havoc($mem m)`
    * Assigns arbitrary values to memory locations in `m`
* `$atomic_f $system $mem $mem_unary_widening($mem m)`
    * Returns an over-approximated super-set of `m`. The over-approximation is done by the model checker in order to make state space finite.
* `$atomic_f $system void $mem_assign_from($state s, $mem m)`
    * deprecated

## op {#op}

Header: [op.h](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/op.h).
Uses: (nothing).

Defines an enumerated type `$operation` with the following values:

| Constant  | Description            |
|-----------|------------------------|
| `_NO_OP`  | no operation           |
| `_MAX`    | maximum                |
| `_MIN`    | minimum                |
| `_SUM`    | sum                    |
| `_PROD`   | product                |
| `_LAND`   | logical and            |
| `_BAND`   | bit-wise and           |
| `_LOR`    | logical or             |
| `_BOR`    | bit-wise or            |
| `_LXOR`   | logical exclusive or   |
| `_BXOR`   | bit-wise exclusive or  |
| `_MINLOC` | min value and location |
| `_MAXLOC` | max value and location |
| `_EQ`     | equal to               |
| `_NEQ`    | not Equal to           |

## pointer {#pointer}

Header: [pointer.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/pointer.cvh).
Uses: [op](#op).

* `$system void $set_default(void *obj);`
    * Updates the leaf nodes of a status variable to the default value 0
* `$system void $apply(void *obj1, $operation op, void *obj2, void *result);`
    * Applies the operation op on `obj1` and `obj2` and stores the result
* `$system _Bool $equals(void *x, void *y);`
    * Are the object pointed to equal?
* `$system void $assert_equals(void *x, void *y, ...);`
    * Assert that the two objects pointed are equal
* `$system _Bool $contains(void *obj1, void *obj2);`
    * Semantics: Does the object pointed to by `obj1` contain that pointed to by `obj1`?
* `$system void * $translate_ptr(void *ptr, void *obj);`
    * Translates a pointer into one object to a pointer into a different object with similar structure.
* `$system void $copy(void *ptr, void *value);`
    * Copies the value pointed to by the right operand to the memory location specified by the left operand
* `$system void $leaf_node_ptrs(void *array, void *obj);`
    * copy the references to the leaf nodes of obj to the given array obj: pointer to type T' whose leaf node types are all type T array: pointer to array of pointer to type T
* `$system _Bool $is_identity_ref(void *obj);`
    * returns true if the given pointer is referencing to an object by identity reference
* `$system void $set_leaf_nodes(void *obj, int value);`
    * updates the leaf nodes of the given objects to with the given integer value
* `$system _Bool $leaf_nodes_equal_to(void *obj, int value);`
    * returns true iff all leaf nodes of the given object equal to the given value
* `$system _Bool $has_leaf_node_equal_to(void *obj, int value);`
    * returns true iff at least one leaf nodes of the given object equal to the given value
* `$system _Bool $is_derefable_pointer(void *ptr);`
    * can the given pointer be safely referenced? A derefable pointer is not NULL and is referring to a memory location of some dyscope.
* `$system void * $pointer_add(const void *ptr,  int offset, int type_size);`
    * Gives a pointer addition operation. Note that the input pointer will always be casted to the form of a pointer to a primitive data type.

## scope {#scope}

Header: [scope.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/scope.cvh).
Uses: (nothing).



## seq {#seq}

Header: [seq.cvh](https://github.com/verified-software-lab/civl/blob/main/mods/dev.civl.abc/src/dev/civl/abc/include/seq.cvh).
Uses: (nothing).



## C Standard library

### stdlib

### stdio

### string

