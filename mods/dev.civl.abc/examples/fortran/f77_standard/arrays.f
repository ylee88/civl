c Fortran 77 Standard Sec. 5, Arrays and Substrings
c This uses subscript triplets (a.k.a. array slices) that were only introduced
c in Fortran 90.
      program p
      implicit none
      integer a(6), b(0:5), c(-3:2), d(10:10,6)
      logical h(6)
      complex f(2,3,4,5,6,7)
      h = (/.true., .true., .true., .false., .false., .false./)
c$ civl assert(all(h(1:3)))
c$ civl assert(any(h(3:6)))
c$ civl assert(.not. all(h(1:4)))
c$ civl assert(.not. any(h(4:6)))

      a = (/ 1, 2, 3, 4, 5, 6 /)
      b = a
c$ civl assert(all(a .eq. b))
      c = 0
      c(-3:-1) = a(1:3)
c$ civl assert(all(c .eq. a .eqv. h))
      c(0:) = b(3:5)
c$ civl assert(c(1) .eq. a(5))
      d(10,:) = a
c$ civl assert(sum(d(10,1:2)) .eq. sum(a(1:2)))
      f(2,3,4,5,6,7) = (1,-2)
c$ civl assert(imag(f(2,3,4,5,6,7)) .eq. -2.0)

c$ civl assert(all(a(::2) .eq. (/1,3,5/)))
c$ civl assert(all(a(2:6:2) .eq. (/2,4,6/)))

      a = a + 6
c$ civl assert(all(a .eq. (/6,7,8,9,10,11,12/)))
      a = 2 * b + a
c$ civl assert(all(a .eq. (/8,11,14,17,20,23,26/)))

c N692 Sec. 6.2.2.4.1 "a subscript in a subscript triplet need not be within
c the declared bounds for that dimension if all values used in selecting the
c array elements are within the declared bounds."
c$ civl assert(all(a(2:7:2) .eq. (/2,4,6/)))

c "When the stride is negative, the sequence begins with the first subscript
c and proceeds in increments of the stride down to the smallest such integer
c equal to or greater than the second subscript"
c$ civl assert(all(a(6:1:-2) .eq. (/6,4,2/)))
      end program
