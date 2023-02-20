c Fortran 77 Standard Sec. 11.10, DO_Statement
c Simple example
      program p
      implicit none
      integer i, j
      j = 0
      do 100, i=1,10
        j = j + 1
  100 continue
c$ civl assert(j==10)
c$ civl assert(i==11)
      end program
