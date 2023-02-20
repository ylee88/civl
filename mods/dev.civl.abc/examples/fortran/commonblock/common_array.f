!     Common block using an array that is used in
!     a different shape inside SUB
      PROGRAM MAIN
      INTEGER A
      REAL    F,R,X,Y
      COMMON /MC/ R(2,3),A,F
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      WRITE(*,*) F
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A(6),B,P,Q
      COMMON /MC/ A,I,B
      B = I + 2*A(6)
      END
