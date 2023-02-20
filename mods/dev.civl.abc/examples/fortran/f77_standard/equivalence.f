c F77 standard Sec. 8.2: An EQUIVALENCE statement is used to specify the
c sharing of  storage  units by two or more entities in a program unit.
      program p
      implicit none
      integer i(4), j(3)
      real a(2,2),b(4),y(2)
      complex z(1)
      equivalence (i(1), j), (a(1,1),b(1)), (y(1),z(1))
      i = 0
      j = 1
c$ civl assert(all(i .eq. (/1,1,1,0/)))
      b(3) = 2
c$ civl assert(a(1,2) .eq. 2)
      y = (/4,5/)
c$ civl assert(imag(z) .eq. 5)
      end program
