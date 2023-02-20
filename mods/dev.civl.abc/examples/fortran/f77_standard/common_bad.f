c Common blocks that go out of scope become undefined.
      program p
      call sub
      call sub2
      end program

      subroutine sub
      integer z
      common /ins/ z
      z = 3
      end subroutine
      
      subroutine sub2
      integer z
      common /ins/ z
c The value of z is undefined
      print *, z
      end subroutine
