c Fortran 77 Standard Sec. 3.1.16
c A blank character has no meaning
      prog ram p
      impl icitnone
      integ er i, j

      j = 0
      do100, i=1,1 8
        j = j + 1
  100 continue
c$ civl assert(i==19)

      j = 0
      do 1 01, i=1,18
        j = j + 1
  101 c o n t i n u e
c$ civl assert(i==19)
      end program
