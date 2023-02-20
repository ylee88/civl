c A parameter must never be modified.
      subroutine sub(a)
      integer a
      a = a + 1
      end subroutine

      program p
      integer i
      parameter(i=3)
      call sub(i)
      end program
