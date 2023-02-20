c Fortran 77 Standard Sec. 5.1.2.2 Dummy_Array_Declarator
c Dummy arrays may have adjustable or assumed size
      subroutine sub(a, b, c, d, e, f, g, low, high)
      implicit none
      integer low, high
      integer a(low:high), b(high), c(*), d(low:*), e(1,*)
      integer f(4), g(2,2)
      integer correct(6)
      correct = (/1,2,3,4,5,6/)
c$ civl assert(all(a .eq. correct(1:3)))
c$ civl assert(all(a(2:4) .eq. correct(1:3)))
c$ civl assert(all(b .eq. correct(1:4)))
c$ civl assert(all(b(1:4) .eq. correct(1:4)))
c$ civl assert(all(c(1:6) .eq. correct))
c$ civl assert(all(d(2:7) .eq. correct))
c$ civl assert(all(e(1,1:6) .eq. correct))
c Dummy arrays can be smaller than the actual argument
c$ civl assert(all(f .eq. correct(1:4)))
c Dummy arrays can have a shape that differs from that of the actual argument
c$ civl assert(all(g .eq. correct(1:4)))

c Sec. 5.5: the variables  involved in  an  adjustable dimension may be
c redefined or become undefined during execution of the external procedure
c with no effect on the above-mentioned properties.
      low = 1
      high = 1
c$ civl assert(all(a .eq. correct(1:3)))
      a = (/7,8,9/)
      end subroutine

      program p
      implicit none
      integer a(6), b(6), c(6), d(6), e(6), f(6), g(6)
      a = (/1,2,3,4,5,6/)
      b = a
      c = a
      d = a
      e = a
      f = a
      g = a
      call sub(a(1:3),b(1:4),c,d,e,f,g,2,4)
c$ civl assert(all(a(1:3) .eq. (/7,8,9/)))
c$ civl assert(all(a(4:6) .eq. (/4,5,6/)))
      end program
