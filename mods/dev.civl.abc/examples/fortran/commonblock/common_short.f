! Using a common block where the first 8 bytes are taken by
! a double precision variable in MAIN, and by two reals in
! SUB. Also, the last variable in the common block (PAD2)
! is not made visible inside SUB. 
      PROGRAM MAIN
      INTEGER A
      REAL    F,R,X,Y
      DOUBLE PRECISION PAD, PAD2
      COMMON PAD,R,A,F,PAD2
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      PAD = F
      WRITE(*,*) PAD
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A,B,P,Q,PAD,PAD2
      COMMON PAD,PAD2,A,I,B
      PAD = A
      PAD2 = I
      B = PAD2 + 2*PAD
      END
