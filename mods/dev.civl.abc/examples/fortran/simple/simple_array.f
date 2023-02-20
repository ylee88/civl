      SUBROUTINE ARRSUM (N, M, ARR, ISUM)
      INTEGER I,J,N,M,ISUM
      INTEGER ARR(N,M)
      
      ISUM = 0
      DO J=1,M
        DO I=1,N
          ISUM = ISUM + ARR(I,J)
        END DO
      END DO
      END SUBROUTINE

      PROGRAM FARRAY
      
      INTEGER N,M,I,J, SUM, EXP
      INTEGER A(1:2,2:3)
      
      N = 2
      M = 2
      EXP = 106
      DO J=2,3
        DO I=1,2
          A(I,J) = J*10 + I
        END DO
      END DO
      
      DO I=1,2
        DO J=2,3
           PRINT *, A(I,J)
        END DO
      END DO
      
      CALL ARRSUM(N,M,A,SUM)
      
!$CVL $assert(SUM == EXP);
      PRINT *, SUM
      
      END PROGRAM