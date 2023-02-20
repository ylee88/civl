c Fortran traps: Logical expressions are not necessarily short-circuited

c     Calling this function triggers an assertion violation
      function unsafe()
      logical unsafe
c$ civl assert(.false.)
      unsafe = .false.
      return
      end function

      program p
      implicit none
      integer i, j
c     CIVL should detect that the function may get called
      if(.true. .or. unsafe()) then
      i = 0
      end if
      end program
