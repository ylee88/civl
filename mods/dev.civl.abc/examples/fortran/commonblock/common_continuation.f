!     COMMON block with continuation in SUB. The two
!     lines referencing MC are treated as one line
!     with the combined list of variables.
      PROGRAM MAIN
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
      COMMON /MC/ A,I
      COMMON /MC/ B
      B = I + 2*A
      END
