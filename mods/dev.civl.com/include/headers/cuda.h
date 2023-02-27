
/* Functions in this file are meant to serve as drop-in CIVL replacements
 * for the Cuda function of the same name. Because of this, much of the
 * documentation of these functions is identical to the documentation
 * for its Cuda counterpart.
 */

#ifndef _CUDA
#define _CUDA

#include <civl-cuda.cvh>

/* Returns in *count the number of devices with compute capability 
 * greater or equal to 1.0 that are available for execution.
 */
cudaError_t cudaGetDeviceCount(int *count);

/* Returns in *device the current devie for the calling host thread
 */
cudaError_t cudaGetDevice(int * device);

/* Returns in *prop the properties of device dev
 */
cudaError_t cudaGetDeviceProperties(struct cudaDeviceProp * prop, int dev);

/* Creates and event object
 */
cudaError_t cudaEventCreate(cudaEvent_t *event);

/* Records an event. If stream is non-zero, the event is recorded 
 * after all preceding operations in stream have been completed; 
 * otherwise, it is recorded after all preceding operations in the 
 * CUDA context have been completed. Since operation is asynchronous, 
 * cudaEventQuery() and/or cudaEventSynchronize() must be used to 
 * determine when the event has actually been recorded.
 */
cudaError_t cudaEventRecord(cudaEvent_t event, cudaStream_t s);

/* Query the status of all device work preceding the most recent call 
 * to cudaEventRecord() (in the appropriate compute streams, as 
 * specified by the arguments to cudaEventRecord()).
 * 
 * If this work has successfully been completed by the device, or if 
 * cudaEventRecord() has not been called on event, then cudaSuccess 
 * is returned. If this work has not yet been completed by the device 
 * then cudaErrorNotReady is returned.
 */
cudaError_t cudaEventQuery(cudaEvent_t event);


/* Wait until the completion of all device work preceding the most 
 * recent call to cudaEventRecord() (in the appropriate compute streams, 
 * as specified by the arguments to cudaEventRecord()).
 *
 * If cudaEventRecord() has not been called on event, cudaSuccess 
 * is returned immediately.
 */
cudaError_t cudaEventSynchronize(cudaEvent_t event);

/* since "timing" doesn't really make sense in the verification process
 * I'm not sure what this should do. maybe it shouldn't exist.
 */
cudaError_t cudaEventElapsedTime(float *t, cudaEvent_t from, cudaEvent_t to);

/* Destroys the event specified by event.
 */
cudaError_t cudaEventDestroy(cudaEvent_t event);

/* Creates a new asynchronous stream.
 */
cudaError_t cudaStreamCreate(cudaStream_t *pStream);


/* Blocks until stream has completed all operations.
 */
cudaError_t cudaStreamSynchronize(cudaStream_t stream);


/* Destroys and cleans up the asynchronous stream specified by stream.
 */
cudaError_t cudaStreamDestroy(cudaStream_t pStream);

/* Explicitly destroys and cleans up all resources associated with the 
 * current device in the current process. Any subsequent API call to 
 * this device will reinitialize the device.
 */
cudaError_t cudaDeviceReset( void );

/* locks until stream has completed all operations.
 */
cudaError_t cudaDeviceSynchronize( void );

/* Copies count bytes from the memory area pointed to by src to the 
 * memory area pointed to by dst, where kind is one of 
 * cudaMemcpyHostToHost, cudaMemcpyHostToDevice, cudaMemcpyDeviceToHost, 
 * or cudaMemcpyDeviceToDevice, and specifies the direction of the 
 * copy. The memory areas may not overlap.
 */
cudaError_t cudaMemcpy ( void *dst, const void *src, size_t count, enum cudaMemcpyKind kind );

/* Not implemented. Prototype provided for compiling purposes.
 */
cudaError_t cudaMalloc( void *ptr, size_t size);

/* Fills the first count bytes of the memory area pointed to by devPtr 
 * with the constant byte value value
 */
cudaError_t cudaMemset(void * devPtr, int value, size_t count);

/* Frees the memory space pointed to by devPtr. Similar semantics to free/$free.
 */
cudaError_t cudaFree(void *devPtr);

/* Sets device as the current device for the calling host thread. Currently,
 * only a single device is supported, so this call always succeeds with a noop.
 */
cudaError_t cudaSetDevice(int device_id);

/* Returns the message string from an error code
 */
const char* cudaGetErrorString(cudaError_t error);

/* Returns the last error that has been produces by any of the runtime calls 
 * in the same host thread and resets it to cudaSuccess
 */
cudaError_t cudaGetLastError(void);

/* DEPRECATED. DO NOT USE
 */
cudaError_t cudaThreadExit(void);

/* Not implemented. Prototype provided for compatibilty purposes
 */
void __syncthreads( void );

uint3 threadIdx;
uint3 blockIdx;
dim3 gridDim;
dim3 blockDim;
int warpSize;

int __shfl_sync(unsigned mask, int var, int srcLane, ...);
int __shfl_up_sync(unsigned mask, int var, unsigned int delta, ...);
int __shfl_down_sync(unsigned mask, int var, unsigned int delta, ...);
int __shfl_xor_sync(unsigned mask, int var, int laneMask, ...);


/** C++ Language Extensions **/

/* atomicAdd()
 * Reads the 16-bit, 32-bit or 64-bit word old located at the address address in
 * global or shared memory, computes (old + val), and stores the result back to
 * memory at the same address. These three operations are performed in one atomic
 * transaction. The function returns old.
 */
int cudaAtomicAdd_int(int* address, int val);
unsigned int cudaAtomicAdd_uint(unsigned int* address, unsigned int val);
unsigned long long int cudaAtomicAdd_ullint(unsigned long long int* address,
                                        unsigned long long int val);
float cudaAtomicAdd_float(float* address, float val);
double cudaAtomicAdd_double(double* address, double val);
#define atomicAdd(X,Y) _Generic(X,              \
    default : cudaAtomicAdd_int,                    \
    unsigned int* : cudaAtomicAdd_uint,             \
    unsigned long long int* : cudaAtomicAdd_ullint, \
    float* : cudaAtomicAdd_float,                   \
    double* : cudaAtomicAdd_double) (X,Y)

/* atomicSub()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes (old - val), and stores the result back to memory at the same
 * address. These three operations are performed in one atomic transaction. The
 * function returns old.
 */
int cudaAtomicSub_int(int* address, int val);
unsigned int cudaAtomicSub_uint(unsigned int* address, unsigned int val);
#define atomicSub(X,Y) _Generic X, \
    default : cudaAtomicSub_int,       \
    unsigned int* : cudaAtomicSub_uint) (X,Y)

/* atomicExch()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory and stores val back to memory at the same address. These two
 * operations are performed in one atomic transaction. The function returns old. 
 */
int cudaAtomicExch_int(int* address, int val);
unsigned int cudaAtomicExch_uint(unsigned int* address, unsigned int val);
unsigned long long int cudaAtomicExch_ullint(unsigned long long int* address,
                                  unsigned long long int val);
float cudaAtomicExch_float(float* address, float val);
#define atomicExch(X,Y) _Generic(X,             \
    default : cudaAtomicExch_int,                   \
    unsigned int* : cudaAtomicExch_uint,            \
    unsigned long long int* : cudaAtomicExch_ullint \
    float* : cudaAtomicExch_float) (X,Y)

/* atomicMin()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory, computes the minimum of old and val, and stores the result
 * back to memory at the same address. These three operations are performed in one
 * atomic transaction. The function returns old.
 */
int cudaAtomicMin_int(int* address, int val);
unsigned int cudaAtomicMin_uint(unsigned int* address, unsigned int val);
unsigned long long int cudaAtomicMin_ullint(unsigned long long int* address,
                                        unsigned long long int val);
#define atomicMin(X,Y) _Generic(X,  \
    default : cudaAtomicMin_int,        \
    unsigned int* : cudaAtomicMin_uint, \
    unsigned long long int* : cudaAtomicMin_ullint) (X,Y)

/* atomicMax()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory, computes the maximum of old and val, and stores the result
 * back to memory at the same address. These three operations are performed in one
 * atomic transaction. The function returns old.
 */
int cudaAtomicMax_int(int* address, int val);
unsigned int cudaAtomicMax_uint(unsigned int* address, unsigned int val);
unsigned long long int cudaAtomicMax_ullint(unsigned long long int* address,
                                        unsigned long long int val);
#define atomicMax(X,Y) _Generic(X,  \
    default : cudaAtomicMax_int,        \
    unsigned int* : cudaAtomicMax_uint, \
    unsigned long long int* : cudaAtomicMax_ullint) (X,Y)

/* atomicInc()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes ((old >= val) ? 0 : (old+1)), and stores the result back to
 * memory at the same address. These three operations are performed in one atomic
 * transaction. The function returns old.
 */
unsigned int atomicInc(unsigned int* address, unsigned int val);

/* atomicDec()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes (((old == 0) || (old > val)) ? val : (old-1) ), and stores
 * the result back to memory at the same address. These three operations are
 * performed in one atomic transaction. The function returns old.
 */
unsigned int atomicDec(unsigned int* address, unsigned int val);

/* atomicCAS()
 * reads the 16-bit, 32-bit or 64-bit word old located at the address address in
 * global or shared memory, computes (old == compare ? val : old) , and stores the
 * result back to memory at the same address. These three operations are performed
 * in one atomic transaction. The function returns old (Compare And Swap).
 */
int cudaAtomicCAS_int(int* address, int compare, int val);
unsigned int cudaAtomicCAS_uint(unsigned int* address,
                            unsigned int compare,
                            unsigned int val);
unsigned long long int cudaAtomicCAS_ullint(unsigned long long int* address,
                                        unsigned long long int compare,
                                        unsigned long long int val);
unsigned short int cudaAtomicCAS_usint(unsigned short int* address,
                                   unsigned short int compare,
                                   unsigned short int val);
#define atomicCAS(address, compare, val) _Generic(address, \
    default : cudaAtomicCAS_int,                               \
    unsigned int* : cudaAtomicCAS_uint,                        \
    unsigned long long int* : cudaAtomicCAS_ullint,            \
    unsigned short int* : cudaAtomicCAS_usint) (address, compare, val)

#endif
