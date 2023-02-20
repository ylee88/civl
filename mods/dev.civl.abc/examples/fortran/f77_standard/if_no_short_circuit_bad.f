c Fortran traps: Logical expressions are not necessarily short-circuited

      function makesafe(i)
      logical makesafe
      integer i
      i = 1
      makesafe = .true.
      return
      end function

      program p
      implicit none
      integer i, j
      i = 0
      if(.true. .or. makesafe()) then
c     This is a division by zero if makesafe was not executed.
c     Whether or not it gets executed is not defined.
      j = 1 / i
      end if
      end program
