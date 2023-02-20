!     This program accidentally uses the "wrong" common
!     block in SUB, resulting in uninitialized variables
!     being used.
      PROGRAM MAIN
      INTEGER A
      REAL    F,R,X,Y
      COMMON /MC/ R,A,F
      COMMON /MCD/ RD,AD,FD
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      WRITE(*,*) F
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A,B,P,Q
      COMMON /MCD/ A,I,B
      B = I + 2*A
      END
