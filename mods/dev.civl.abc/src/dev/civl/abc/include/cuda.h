
/* Functions in this file are meant to serve as drop-in CIVL replacements
 * for the Cuda function of the same name. Because of this, much of the
 * documentation of these functions is identical to the documentation
 * for its Cuda counterpart.
 */

#ifndef _CUDA
#define _CUDA

#include <civl-cuda.cvh>

/* the type returned by all Cuda functions
 */
typedef enum {
  cudaSuccess,
  cudaErrorMissingConfiguration,
  cudaErrorMemoryAllocation,
  cudaErrorInitializationError,
  cudaErrorLaunchFailure,
  cudaErrorPriorLaunchFailure,
  cudaErrorLaunchTimeout,
  cudaErrorLaunchOutOfResources,
  cudaErrorInvalidDeviceFunction,
  cudaErrorInvalidConfiguration,
  cudaErrorInvalidDevice,
  cudaErrorInvalidValue,
  cudaErrorInvalidPitchValue,
  cudaErrorInvalidSymbol,
  cudaErrorMapBufferObjectFailed,
  cudaErrorUnmapBufferObjectFailed,
  cudaErrorInvalidHostPointer,
  cudaErrorInvalidDevicePointer,
  cudaErrorInvalidTexture,
  cudaErrorInvalidTextureBinding,
  cudaErrorInvalidChannelDescriptor,
  cudaErrorInvalidMemcpyDirection,
  cudaErrorAddressOfConstant,
  cudaErrorTextureFetchFailed,
  cudaErrorTextureNotBound,
  cudaErrorSynchronizationError,
  cudaErrorInvalidFilterSetting,
  cudaErrorInvalidNormSetting,
  cudaErrorMixedDeviceExecution,
  cudaErrorCudartUnloading,
  cudaErrorUnknown,
  cudaErrorNotYetImplemented,
  cudaErrorMemoryValueTooLarge,
  cudaErrorInvalidResourceHandle,
  cudaErrorNotReady,
  cudaErrorInsufficientDriver,
  cudaErrorSetOnActiveProcess,
  cudaErrorInvalidSurface,
  cudaErrorNoDevice,
  cudaErrorECCUncorrectable,
  cudaErrorSharedObjectSymbolNotFound,
  cudaErrorSharedObjectInitFailed,
  cudaErrorUnsupportedLimit,
  cudaErrorDuplicateVariableName,
  cudaErrorDuplicateTextureName,
  cudaErrorDuplicateSurfaceName,
  cudaErrorDevicesUnavailable,
  cudaErrorInvalidKernelImage,
  cudaErrorNoKernelImageForDevice,
  cudaErrorIncompatibleDriverContext,
  cudaErrorPeerAccessAlreadyEnabled,
  cudaErrorPeerAccessNotEnabled,
  cudaErrorDeviceAlreadyInUse,
  cudaErrorProfilerDisabled,
  cudaErrorProfilerNotInitialized,
  cudaErrorProfilerAlreadyStarted,
  cudaErrorProfilerAlreadyStopped,
  cudaErrorAssert,
  cudaErrorTooManyPeers,
  cudaErrorHostMemoryAlreadyRegistered,
  cudaErrorHostMemoryNotRegistered,
  cudaErrorOperatingSystem,
  cudaErrorStartupFailure,
  cudaErrorApiFailureBase
} cudaError;
typedef cudaError cudaError_t;

/* struct representing the properties of a Cuda device
 */
typedef struct cudaDeviceProp {
  char name[256];
  size_t totalGlobalMem;
  size_t sharedMemPerBlock;
  int regsPerBlock;
  int warpSize;
  size_t memPitch;
  int maxThreadsPerBlock;
  int maxThreadsDim[3];
  int maxGridSize[3];
  int clockRate;
  size_t totalConstMem;
  int major;
  int minor;
  size_t textureAlignment;
  size_t texturePitchAlignment;
  int deviceOverlap;
  int multiProcessorCount;
  int kernelExecTimeoutEnabled;
  int integrated;
  int canMapHostMemory;
  int computeMode;
  int maxTexture1D;
  int maxTexture1DLinear;
  int maxTexture2D[2];
  int maxTexture2DLinear[3];
  int maxTexture2DGather[2];
  int maxTexture3D[3];
  int maxTextureCubemap;
  int maxTexture1DLayered[2];
  int maxTexture2DLayered[3];
  int maxTextureCubemapLayered[2];
  int maxSurface1D;
  int maxSurface2D[2];
  int maxSurface3D[3];
  int maxSurface1DLayered[2];
  int maxSurface2DLayered[3];
  int maxSurfaceCubemap;
  int maxSurfaceCubemapLayered[2];
  size_t surfaceAlignment;
  int concurrentKernels;
  int ECCEnabled;
  int pciBusID;
  int pciDeviceID;
  int pciDomainID;
  int tccDriver;
  int asyncEngineCount;
  int unifiedAddressing;
  int memoryClockRate;
  int memoryBusWidth;
  int l2CacheSize;
  int maxThreadsPerMultiProcessor;
} cudaDeviceProp;

typedef $cuda_stream_t cudaStream_t;

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
cudaError_t cudaMemcpy ( void *dst, const void *src, size_t count, cudaMemcpyKind kind );

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

/* Dummy declaration for the complier. Implemented via transformation.
 */
void __syncthreads( void );
int __syncthreads_count(int pred);
int __syncthreads_or(int pred);
int __syncthreads_and(int pred);

int __popc(unsigned int x);
int __popcll(unsigned long long int x);

int max(int x, int y);
int min(int x, int y);

uint3 threadIdx;
uint3 blockIdx;
dim3 gridDim;
dim3 blockDim;

#define $GET_ARG_1(_1, ...) _1
#define $GET_ARG_2(_1, _2, ...) _2

#define __syncwarp() $cuda__syncwarp($thread)

#define $CUDA_SHFL_PARAM_MACRO(...) $GET_ARG_1(__VA_ARGS__, warpSize, 0), $GET_ARG_2(__VA_ARGS__, warpSize, 0)

#define __shfl_sync(mask, var, ...)                                     \
  _Generic(var,                                                         \
           default: $cuda__shfl_sync_int,                               \
           unsigned int: $cuda__shfl_sync_uint,                         \
           long: $cuda__shfl_sync_long,                                 \
           unsigned long: $cuda__shfl_sync_ulong,                       \
           long long: $cuda__shfl_sync_ll,                              \
           unsigned long long: $cuda__shfl_sync_ull,                    \
           float: $cuda__shfl_sync_float,                               \
           double: $cuda__shfl_sync_double) (mask, var, $CUDA_SHFL_PARAM_MACRO(__VA_ARGS__), $thread)

#define __shfl_up_sync(mask, var, ...)                                  \
  _Generic(var,                                                         \
           default: $cuda__shfl_up_sync_int,                            \
           unsigned int: $cuda__shfl_up_sync_uint,                      \
           long: $cuda__shfl_up_sync_long,                              \
           unsigned long: $cuda__shfl_up_sync_ulong,                    \
           long long: $cuda__shfl_up_sync_ll,                           \
           unsigned long long: $cuda__shfl_up_sync_ull,                 \
           float: $cuda__shfl_up_sync_float,                            \
           double: $cuda__shfl_up_sync_double) (mask, var, $CUDA_SHFL_PARAM_MACRO(__VA_ARGS__), $thread)

#define __shfl_down_sync(mask, var, ...)                                \
  _Generic(var,                                                         \
           default: $cuda__shfl_down_sync_int,                          \
           unsigned int: $cuda__shfl_down_sync_uint,                    \
           long: $cuda__shfl_down_sync_long,                            \
           unsigned long: $cuda__shfl_down_sync_ulong,                  \
           long long: $cuda__shfl_down_sync_ll,                         \
           unsigned long long: $cuda__shfl_down_sync_ull,               \
           float: $cuda__shfl_down_sync_float,                          \
           double: $cuda__shfl_down_sync_double) (mask, var, $CUDA_SHFL_PARAM_MACRO(__VA_ARGS__), $thread)
 
#define __shfl_xor_sync(mask, var, ...)                                 \
  _Generic(var,                                                         \
           default: $cuda__shfl_xor_sync_int,                           \
           unsigned int: $cuda__shfl_xor_sync_uint,                     \
           long: $cuda__shfl_xor_sync_long,                             \
           unsigned long: $cuda__shfl_xor_sync_ulong,                   \
           long long: $cuda__shfl_xor_sync_ll,                          \
           unsigned long long: $cuda__shfl_xor_sync_ull,                \
           float: $cuda__shfl_xor_sync_float,                           \
           double: $cuda__shfl_xor_sync_double) (mask, var, $CUDA_SHFL_PARAM_MACRO(__VA_ARGS__), $thread)

#define __ballot_sync(mask, predicate) $cuda__ballot_sync(mask, predicate, $thread)
#define __all_sync(mask, predicate) $cuda__all_sync(mask, predicate, $thread)
#define __any_sync(mask, predicate) $cuda__any_sync(mask, predicate, $thread)

/** C++ Language Extensions **/

/* atomicAdd()
 * Reads the 16-bit, 32-bit or 64-bit word old located at the address address in
 * global or shared memory, computes (old + val), and stores the result back to
 * memory at the same address. These three operations are performed in one atomic
 * transaction. The function returns old.
 */
#define atomicAdd(X,Y) _Generic(X,                    \
    default : $cuda_atomicAdd_int,                    \
    unsigned int* : $cuda_atomicAdd_uint,             \
    unsigned long long int* : $cuda_atomicAdd_ullint, \
    float* : $cuda_atomicAdd_float,                   \
    double* : $cuda_atomicAdd_double) (X, Y, $thread)

/* atomicSub()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes (old - val), and stores the result back to memory at the same
 * address. These three operations are performed in one atomic transaction. The
 * function returns old.
 */
#define atomicSub(X,Y) _Generic(X,       \
    default : $cuda_atomicSub_int,       \
    unsigned int* : $cuda_atomicSub_uint) (X, Y, $thread)

/* atomicExch()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory and stores val back to memory at the same address. These two
 * operations are performed in one atomic transaction. The function returns old. 
 */
#define atomicExch(X,Y) _Generic(X,                    \
    default : $cuda_atomicExch_int,                    \
    unsigned int* : $cuda_atomicExch_uint,             \
    unsigned long long int* : $cuda_atomicExch_ullint, \
    float* : $cuda_atomicExch_float) (X, Y, $thread)

/* atomicMin()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory, computes the minimum of old and val, and stores the result
 * back to memory at the same address. These three operations are performed in one
 * atomic transaction. The function returns old.
 */
#define atomicMin(X,Y) _Generic(X,        \
    default : $cuda_atomicMin_int,        \
    unsigned int* : $cuda_atomicMin_uint, \
    unsigned long long int* : $cuda_atomicMin_ullint) (X, Y, $thread)

/* atomicMax()
 * reads the 32-bit or 64-bit word old located at the address address in global
 * or shared memory, computes the maximum of old and val, and stores the result
 * back to memory at the same address. These three operations are performed in one
 * atomic transaction. The function returns old.
 */
#define atomicMax(X,Y) _Generic(X,        \
    default : $cuda_atomicMax_int,        \
    unsigned int* : $cuda_atomicMax_uint, \
    unsigned long long int* : $cuda_atomicMax_ullint) (X, Y, $thread)

/* atomicInc()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes ((old >= val) ? 0 : (old+1)), and stores the result back to
 * memory at the same address. These three operations are performed in one atomic
 * transaction. The function returns old.
 */
#define atomicInc(address, val) $cuda_atomicInc(address, val, $thread)

/* atomicDec()
 * reads the 32-bit word old located at the address address in global or shared
 * memory, computes (((old == 0) || (old > val)) ? val : (old-1) ), and stores
 * the result back to memory at the same address. These three operations are
 * performed in one atomic transaction. The function returns old.
 */
#define atomicDec(address, val) $cuda_atomicDec(address, val, $thread)

/* atomicCAS()
 * reads the 16-bit, 32-bit or 64-bit word old located at the address address in
 * global or shared memory, computes (old == compare ? val : old) , and stores the
 * result back to memory at the same address. These three operations are performed
 * in one atomic transaction. The function returns old (Compare And Swap).
 */
#define atomicCAS(address, compare, val) _Generic(address, \
    default : $cuda_atomicCAS_int,                               \
    unsigned int* : $cuda_atomicCAS_uint,                        \
    unsigned long long int* : $cuda_atomicCAS_ullint,            \
    unsigned short int* : $cuda_atomicCAS_usint) (address, compare, val, $thread)
    
/*
 * reads the 32-bit or 64-bit word old located at the address address
 * in global or shared memory, computes (old & val), and stores the
 * result back to memory at the same address. These three operations
 * are performed in one atomic transaction. The function returns old.
 */
#define atomicAnd(address, val) _Generic(address, \
    default : $cuda_atomicAnd_int,                \
    unsigned int* : $cuda_atomicAnd_uint,         \
    unsigned long long int* : $cuda_atomicAnd_ullint) (address, val, $thread)

/*
 * reads the 32-bit or 64-bit word old located at the address address
 * in global or shared memory, computes (old | val), and stores the
 * result back to memory at the same address. These three operations
 * are performed in one atomic transaction. The function returns old.
 */
#define atomicOr(address, val) _Generic(address, \
    default : $cuda_atomicOr_int,                \
    unsigned int* : $cuda_atomicOr_uint,         \
    unsigned long long int* : $cuda_atomicOr_ullint) (address, val, $thread)

/*
 * reads the 32-bit or 64-bit word old located at the address address
 * in global or shared memory, computes (old ^ val), and stores the
 * result back to memory at the same address. These three operations
 * are performed in one atomic transaction. The function returns old.
 */
#define atomicXor(address, val) _Generic(address, \
    default : $cuda_atomicXor_int,                \
    unsigned int* : $cuda_atomicXor_uint,         \
    unsigned long long int* : $cuda_atomicXor_ullint) (address, val, $thread)

#endif
