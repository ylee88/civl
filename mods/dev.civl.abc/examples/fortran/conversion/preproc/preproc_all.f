#include "macros.h"

C      A Fortran program example used for testing that
C      ABC shall correctly handle C preprocessor directives
C      defined in Fortran source code.
C      Directives are used mainly for two purposes:
C        1. User-specified conditional compilation
C        2. Macro expansion / String substitution
C      Directives tested here are: 
C        "include", "define", "undef", "if", "ifdef", "ifndef"
C        "error"

       program HelloWorld
#if NUM_MSG==1
          print *, MSG_HW
#elif NUM_MSG==2
          print *, MSG_HW
          print *, MSG_HI, NAME
#elif NUM_MSG>=3
          print *, MSG_HW
          print *, MSG_HI, NAME
#ifdef BYE
          print *, MSG_BW
#undef BYE
#endif
#ifdef BYE
#error "BYE should be undefined." 
#endif
#else
          print *, MSG_NM
#endif
       end
