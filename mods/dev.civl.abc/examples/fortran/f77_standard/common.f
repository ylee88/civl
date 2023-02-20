c F77 standard Sec. 8.3 COMMON statement
      program p
      integer i
      real a,b,x,y,w,z
      common b,i,a
      common /cm/ w,z
      i = 2
      b = 3.1
      a = 4.2
      x = 5.3
      y = 6.4
      call sub(x,y)
c$ civl assert(i .eq. 3)
c$ civl assert(a .eq. 5.2)
c$ civl assert(b .eq. 4.1)
c$ civl assert(x .eq. 6.3)
c$ civl assert(y .eq. 7.4)
      call sub2
c$ civl assert(i .eq. 3)
c$ civl assert(a .eq. 5.2)
c$ civl assert(b .eq. 4.1)
c$ civl assert(x .eq. 6.3)
c$ civl assert(y .eq. 7.4)
      end program

      subroutine sub(p,q)
      integer i
      real a,b,p,q,y,z
      common  a,i,b, /cm/ y,z
c$ civl assert(p .eq. 5.3)
c$ civl assert(q .eq. 6.4)
c$ civl assert(a .eq. 3.1)
c$ civl assert(i .eq. 2)
c$ civl assert(b .eq. 4.2)
      p = p + 1
      q = q + 1
      a = a + 1
      b = b + 1
      i = i + 1
      y = 7.5
      z = 8.6
      end subroutine

      subroutine sub2
      real z,y
c The list following each  successive  appearance  of  the same common block
c name is treated as a continuation of the list for  that common block name.
      common /cm/ z
      common /cm/ y
c$ civl assert(y .eq. 8.6)
c$ civl assert(z .eq. 7.5)
      end subroutine
