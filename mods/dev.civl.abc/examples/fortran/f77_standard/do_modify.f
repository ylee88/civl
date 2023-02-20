c Fortran 77 Standard Sec. 11.10, DO_Statement
c Loops with strieded counter
      program p
      implicit none
      integer counter, lower, upper, stride, testvar

c     Initial counter value should not matter
      counter = 100000
      testvar = 0
      lower = 5
      upper = 20
      stride = 5
      do 100, counter=lower,upper,stride
        testvar = testvar + 1
c       none of the below should change the number of iterations
        lower = -10
        upper = 5000
        stride = 2
  100 continue
c$ civl assert(counter==25)
c$ civl assert(testvar==4)
      end program
