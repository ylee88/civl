c F77 standard Sec. 8.4.2
c The length of each character array can be specified after the type name.
c Each individual variable name can have its own length that overrides the
c default length.
c The length of a character dummy argument will depend on its actual argument
      subroutine sub(a)
      character a*(*)
c$ civl assert(a .eq. "ab")
      end subroutine

      program p
      implicit none
      character*3 a, b*2
      a = "abc"
      b = a
c$ civl assert(b .eq. "ab")
      call sub(b)
      end program
