c Fortran 77 Standard Sec. 5.1.2.2 Dummy_Array_Declarator
c A dummy argument must not have greater size than the actual argument
      subroutine f(a)
      implicit none
      integer a(7)
      end subroutine

      program p
      implicit none
      integer a(6)
      a = (/1,2,3,4,5,6/)
      call f(a)
      end program
