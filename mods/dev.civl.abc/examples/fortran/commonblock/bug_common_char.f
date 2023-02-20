!     This program contains a character array in a common
!     block, along with other variable types. This is
!     against the F77 standard Sec. 8.3.1:
!     > If a character variable or  character  array  is  in  a
!     > common  block, all of the entities in that common block
!     > must be of type character.
      PROGRAM MAIN
      INTEGER A
      REAL    F,R,X,Y
      character C*17
      COMMON /MC/ R,A,C,F
      A = 5
      R = 3.5
      CALL SUB(X,Y)
      WRITE(*,*) F
      END

      SUBROUTINE SUB(P,Q)
      INTEGER I
      REAL    A,B,P,Q
      character c*17
      COMMON /MC/ A,I
      COMMON /MC/ C,B
      B = I + 2*A
      END
