!     Variables in the nameless comon block stay in scope between
!     calls to SUB
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
      COMMON Y
      IF(FLAG .eq. 1) THEN
        Y = X * 2
      ELSE
        X = Y
      ENDIF
      END
