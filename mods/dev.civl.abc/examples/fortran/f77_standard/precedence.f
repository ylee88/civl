      program p
      implicit none
      double precision res

      res = -2**2
c$ civl assert(res .eq. -4)

      res = -5+4*3**2
c$ civl assert(res .eq. 31)

      res = 2**3**2
c$ civl assert(res .eq. 512)
      end program
