c Example from F77 standard Sec. 8.3.6
c "Equivalence association must not cause a common block storage sequence to be
c extended  by  adding  storage units preceding the first storage unit of the
c first entity specified in a  COMMON statement  for  the  common  block."
      program p
      COMMON /X/A
      REAL B(2)
      EQUIVALENCE (A,B(2))
      end program
