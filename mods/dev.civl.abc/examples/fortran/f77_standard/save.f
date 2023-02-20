c F77 Sec. 8.9: SAVE statement
c The value of a SAVE variable is preserved across calls to a subroutine
c or function.
      subroutine sub(t,b)
      integer a,b
      logical t
      save a
      if(t) then
      a = 2
      else
      a = a + 1
      end if
c$ civl assert(a .eq. b)
      end subroutine
      
      subroutine sub2(t,b)
      integer a,b
      logical t
c SAVE without a variable name preserves all variables in that subroutine
      save
      if(t) then
      a = 2
      else
      a = a + 1
      end if
c$ civl assert(a .eq. b)
      end subroutine
      
      subroutine sub3(t,b)
      integer :: b
      logical :: t
c Initialising a variable in the same line in which it is declared
c implicitly activates the SAVE attribute for this variable.
      integer :: a = 2
      if(.not. t) then
      a = a + 1
      end if
c$ civl assert(a .eq. b)
      end subroutine

      program p
      call sub(.true.,2)
      call sub(.false.,3)
      call sub(.false.,4)
      call sub2(.true.,2)
      call sub2(.false.,3)
      call sub2(.false.,4)
      call sub3(.true.,2)
      call sub3(.false.,3)
      call sub3(.false.,4)
      end program
