!     This is like common_short.f, but the common block
!     has a name here. In this case, it is illegal to use
!     only parts of the common block (i.e. leave some
!     variables undeclared in SUB, in this case, PAD3)
      PROGRAM MAIN
      REAL X
      X = 1.0
      CALL SUB(X,1)
      CALL SUB(X,2)
      WRITE(*,*) X
      END

      SUBROUTINE SUB(X,FLAG)
      INTEGER FLAG
      REAL    X
      COMMON /MC/ Y
      IF(FLAG .eq. 1) THEN
        Y = X * 2
      ELSE
        X = Y
      ENDIF
      END
