c Fortran 77 Standard Sec. 11.10, DO_Statement
c Loops with strieded counter
      program p
      implicit none
      integer i, j

      j = 0
      do 100, i=1,18,3
        j = j + 1
  100 continue
c$ civl assert(j==6)
c$ civl assert(i==19)

      j = 0
      do 101, i=1,19,3
        j = j + 1
  101 continue
c$ civl assert(i==7)
c$ civl assert(i==22)

      j = 0
      do 102, i=1,20,3
        j = j + 1
  102 continue
c$ civl assert(i==7)
c$ civl assert(i==22)

      j = 0
      do 103, i=1,21,3
        j = j + 1
  103 continue
c$ civl assert(i==8)
c$ civl assert(i==22)
      end program
