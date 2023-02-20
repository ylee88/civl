c F77 standard Sec. 8.6:
c The PARAMETER statement is used to give  a  constant a symbolic name.
      program p
      integer i
      logical t
      real a(3)
      parameter(i=3, t=.true., a=(/1,2,3/))
c$ civl assert(i .eq. 3)
c$ civl assert(t)
c$ civl assert(all(a .eq. (/1,2,3/)))
      end program
