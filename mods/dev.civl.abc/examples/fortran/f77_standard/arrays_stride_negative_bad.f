c Fortran 77 Standard Sec. 5.1.2.2 Dummy_Array_Declarator
c The stride in a subscript triplet must not be zero.
      program p
      implicit none
      integer a(6)
      integer stride
      a = (/1,2,3,4,5,6/)
      stride = 0
      print *, a(1:6:stride)
      end program
