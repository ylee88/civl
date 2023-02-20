c F77 standard Sec. 8.3 COMMON statement
c Just like in the equivalence statement, the shape does not need to match.
c Also, the un-named common block does not need the same size everywhere.
      program p
      integer a, b(6), c, d(6)
      common b,a
      common /cm/ c,d
      a = 1
      b = (/1,2,3,4,5,6/)
      c = a
      d = b
      call sub
      end program

      subroutine sub
      integer b(2,3), c, d(2,3)
      common b
      common /cm/ c,d
c$ civl assert(b(2,2) .eq. 4)
c$ civl assert(d(2,2) .eq. 4)
c$ civl assert(c .eq. 1)
      end subroutine
