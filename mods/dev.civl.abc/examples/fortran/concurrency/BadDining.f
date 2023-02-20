       PROGRAM BADDINING
         PARAMETER (N=4)
         COMMON /ALLFORK/ IPICKED,INOTPICKED,IDININGS,IFORKS(N)
       
         IPICKED = 0
         INOTPICKED = 1
         IDININGS = 10
       
         DO I = 1,N,1
           IFORKS(I) = 1
         ENDDO
!$OMP PARALLEL PRIVATE (I)
!$OMP DO 
         DO I = 1,N
           CALL DINE(I)
         END DO
!$OMP END DO
!$OMP END PARALLEL
       END


       SUBROUTINE DINE(X)
         INTEGER L,R,X
         PARAMETER (N=4)
         COMMON /ALLFORK/ IPICKED,INOTPICKED,IDININGS,IFORKS(N)
       
         L = X
         R = IAND(X,3)+1
         DO WHILE (.TRUE.)
           DO WHILE (IFORKS(L) .EQ. IPICKED)
           END DO
           IFORKS(L) = IPICKED
           PRINT *,'P(', X ,') is picking left fork '
           DO WHILE (IFORKS(R) .EQ. IPICKED)
           END DO
           IFORKS(R) = IPICKED
           PRINT *,'P(', X ,') is picking right fork '
           IDININGS = IDININGS - 1
           PRINT *,'P(', X ,') is dining '
           IFORKS(L) = INOTPICKED
           PRINT *,'P(', X ,') is putting down left fork '
           IFORKS(R) = INOTPICKED
           PRINT *,'P(', X ,') is putting down right fork '           
         END DO       
       END