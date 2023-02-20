c F77 standard Sec. 8.1: A DIMENSION statement is used to specify  the
c symbolic names and dimension specifications of arrays.
      program p
      dimension i(4,-1:3), r(2)
      i = 3.5
      r = 3.5
c$ civl assert( all(shape(i) .eq. (/4,5/)) )
c$ civl assert( i(3,0) .eq. 3 )
c$ civl assert( r(1) .eq. 3.5 )
      end program
