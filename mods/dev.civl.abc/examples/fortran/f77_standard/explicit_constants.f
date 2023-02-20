c Fortran 77 Standard Sec. 4, data types and constants
c Declaring and setting a variable of each of F77's types.
      program p
      implicit none
      integer i, ipos, ineg
      real r, rneg, rexp
      double precision d, dneg, dexp
      complex zreal, zint, zmix
      double complex ydbl, yint, ymix
      logical lt, lf
      character c*4
      i = 12
      ipos = +12
      ineg = -24
c$ civl assert(i .eq. ipos .and. -(i+ipos) .eq. ineg)

      r = 12.34
      rneg = -12.34
      rexp = 12.34e-2
c$ civl assert(r .EQ. -rneg .and. r/100 .eq. rexp)

      d = 12.34
      dneg = -12.34
      dexp = -12.34d3
c$ civl assert(-d .EQ. dneg .and. d*1000 .eq. -rexp)

      zreal = (12.34, -12.34e2)
      zint = (12, -12)
      zmix = (12, 12.0)
      ydbl = (12.34, -12.34d2)
      yint = (12, -12)
      ymix = (12, 12.0)
c$ civl assert(zreal .eq. ydbl .and. zint .eq. yint .and. zmix .eq. ymid)
c$ civl assert(ymix .eq. yint .and. zmix .eq. complex(12,12))

      lt = .truE.
      lf = .FaLSE.
c$ civl assert(.not. lt .eqv. lf)

      c = 'abcd'
c$ civl assert(c .eq. 'abcd')

      end program
