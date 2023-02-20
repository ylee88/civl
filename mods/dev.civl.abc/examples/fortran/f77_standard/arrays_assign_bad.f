c Fortran 77
c The size in an assignment must match (except if RHS is a scalar)
      program p
      implicit none
      integer a(6), b(5)
      integer stride
      b = (/1,2,3,4,5/)
      a = b
      end program
