#if 0
  This group has definition related to dimensions. The first three 
  are names of the axes. When referring to all the dimensions at once,
  ALLDIR is used, and MDIM defines the maximum number of dimensions
  supported in the code.
#endif

#define IAXIS 1
#define JAXIS 2
#define KAXIS 3
#define ALLDIR -1
#define MDIM 3

#if 0
  The next two constants are used in connection of integer block boundaries
  LOW refers to the lowest index and HIGH refers to the highest index of the 
  block. These can be used interchangeably for either the whole block 
  including guardcells or for the interior only.
#endif

#define LOW 1
#define HIGH 2
