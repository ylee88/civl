!     COMMON block without a name
      PROGRAM MAIN
      INTEGER A
      REAL    F,R,X,Y
      COMMON R,A,F
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      WRITE(*,*) F
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A,B,P,Q
      COMMON A,I,B
      B = I + 2*A
      END
