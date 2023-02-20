!     Variables in named common blocks drop out of scope
!     (become undefined) at the END statement, unless they
!     are still referenced somewhere else. See F77 Sec 15.8.4
!     The second call therefore reads an uninitialized variable.
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
