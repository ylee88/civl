#define CONSTANT_ZERO (0)
#define CONSTANT_ONE  (1)
#define CONSTANT_TWO  (2)

#define NDIM 1
#define MAXBLOCKS 1000
#define NGUARD 0


#define K1D CONSTANT_ONE
#define K2D CONSTANT_ZERO
#define K3D CONSTANT_ZERO


#define FIXEDBLOCKSIZE 1

#ifdef FIXEDBLOCKSIZE 

#define NXB 8
#define NYB 1
#define NZB 1

#define GRID_ILO_GC CONSTANT_ONE
#define GRID_JLO_GC CONSTANT_ONE
#define GRID_KLO_GC CONSTANT_ONE
        
#define GRID_IHI_GC (NXB + CONSTANT_TWO*NGUARD)
#define GRID_JHI_GC (NYB)
#define GRID_KHI_GC (NZB)
        
#define GRID_ILO (NGUARD + CONSTANT_ONE)
#define GRID_JLO (CONSTANT_ONE)
#define GRID_KLO (CONSTANT_ONE)

#define GRID_IHI (NGUARD + NXB)
#define GRID_JHI (NYB)
#define GRID_KHI (NZB)


#if 0
  ************************************************************************
  !!!DEV: MAXCELLS pertains to all simulations but someone else needs to 
     decide how this works with non fixed block size!

  It is the maxmimum of  GRID_{IJK}HI_GC. We compute the maximum using 
  preprocessor stuff
  ************************************************************************
#endif

#if GRID_IHI_GC > GRID_JHI_GC
#define FLASHPP_MAX_IJ_TEMP GRID_IHI_GC
#else
#define FLASHPP_MAX_IJ_TEMP GRID_JHI_GC
#endif

#if FLASHPP_MAX_IJ_TEMP > GRID_KHI_GC
#define MAXCELLS FLASHPP_MAX_IJ_TEMP
#else
#define MAXCELLS GRID_KHI_GC
#endif

#endif 

