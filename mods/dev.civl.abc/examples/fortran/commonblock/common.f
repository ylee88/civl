      PROGRAM MAIN
!     FLASH4.4/source/flashUtilities/interpolation/oneDim
!     Simple example of a common block. Note that the
!     contents are accessed through different names
!     in MAIN and SUB.
      
      INTEGER A
      REAL    F,R,X,Y
      COMMON /MC/ R,A,F
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      WRITE(*,*) F
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A,B,P,Q
      COMMON /MC/ A,I,B
      B = I + 2*A
      END
