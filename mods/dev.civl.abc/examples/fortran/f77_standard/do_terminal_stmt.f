c Fortran 77 Standard Sec. 11.10, DO_Statement
c Nested loop, separate terminal statements
      program p
      implicit none
      integer i, j, ji, jo, k
      ji = 0
      jo = 0
      do 100, i=1,3
        jo = jo + 1
        do 200, k=1,5
          ji = ji + 1
  200   continue
  100 continue
c$ civl assert(ji==15)
c$ civl assert(jo==3)
c$ civl assert(i==4)
c$ civl assert(k==6)
      end program
