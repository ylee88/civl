c This calls the subroutine in external_intrinsic_helper.f
      program p
      integer i
      external sub
      intrinsic abs
      i = -3
      call sub(i)
      i = abs(i)
c$ civl assert(i .eq. 2)
      end program
