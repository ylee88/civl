      program p
      implicit none
      integer a,b,res
      real ar,br,resr
      double precision ad,bd,resd
      character c1*3,c2*3,c3*6
      logical t,f

      f = 2 .ge. 3
c$ civl assert(.not. f)
      t = 2 .ge. 2
c$ civl assert(t)
      f = 2 .gt. 2
c$ civl assert(.not. f)
      t = 2 .gt. 1
c$ civl assert(t)
      f = 3 .le. 2
c$ civl assert(.not. f)
      t = 2 .le. 2
c$ civl assert(t)
      f = 2 .lt. 2
c$ civl assert(.not. f)
      t = 1 .lt. 2
c$ civl assert(t)
      t = 1 .eq. 1
c$ civl assert(t)
      f = 1 .eq. 2
c$ civl assert(.not. f)
      t = 1 .ne. 2
c$ civl assert(t)
      f = 1 .ne. 1
c$ civl assert(.not. f)

      t = .true. .eqv. .true.
c$ civl assert(t)
      t = .false. .eqv. .false.
c$ civl assert(t)
      f = .false. .eqv. .true.
c$ civl assert(.not. f)
      t = .true. .neqv. .false.
c$ civl assert(t)
      f = .false. .neqv. .false.
c$ civl assert(.not. f)
      f = .true. .neqv. .true.
c$ civl assert(.not. f)


      c1 = "a"
      c2 = "def"
      c3 = c1//c2
c$ civl assert(c3 .eq. "a  def")
c character strings are truncated if assigned to shorter target
      c2 = c3
c$ civl assert(c2 .eq. "abc")
c character strings are padded if assigned to longer target
      c3 = c2
c$ civl assert(c3 .eq. "abc")
c comparison evaluates to .true. regardless of trailing blanks
c$ civl assert(c3 .eq. "abc   ")
c blanks at the start matter though
c$ civl assert(.not. c3 .eq. " abc")

      a = 2
      b = 3

      res = a+b
c$ civl assert(res .eq. 5)
      res = a*b
c$ civl assert(res .eq. 6)
      res = b/a
c$ civl assert(res .eq. 1)
      res = a-b
c$ civl assert(res .eq. -1)
      res = a**b
c$ civl assert(res .eq. 8)
      res = +a
c$ civl assert(res .eq. 2)
      res = -a
c$ civl assert(res .eq. -2)

      ar = a
      br = b
      resr = ar+br
c$ civl assert(resr .eq. 5)
      resr = ar*br
c$ civl assert(resr .eq. 6)
      resr = br/ar
c$ civl assert(resr .eq. 1.5)
      resr = ar-br
c$ civl assert(resr .eq. -1)
      resr = ar**br
c$ civl assert(resr .eq. 8)
      resr = +ar
c$ civl assert(resr .eq. 2)
      resr = -ar
c$ civl assert(resr .eq. -2)

      ad = a
      bd = b
      resd = ad+bd
c$ civl assert(resd .eq. 5)
      resd = ad*bd
c$ civl assert(resd .eq. 6)
      resd = bd/ad
c$ civl assert(resd .eq. 1.5)
      resd = ad-bd
c$ civl assert(resd .eq. -1)
      resd = ad**bd
c$ civl assert(resd .eq. 8)
      resd = +ad
c$ civl assert(resd .eq. 2)
      resd = -ad
c$ civl assert(resd .eq. -2)
      end program
