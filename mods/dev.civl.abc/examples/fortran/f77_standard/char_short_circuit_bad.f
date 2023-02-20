c This function may or may not be called. See F77 standard Sec. 6.6.5
      function c2(a)
      integer a
      character c2*3
      a = 1
      c2 = "test"
      return
      end function

      program p
      implicit none
      character c1*4
      character c2*3
      integer a
      a = 0
c The compiler could short-circuit this: c1 is too short to be affected by c2
      c1 = "test"//c2(a)
c This could divide by zero
      print *, c1, 1/a
      end program
