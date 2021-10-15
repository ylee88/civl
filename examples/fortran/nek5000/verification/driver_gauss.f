      PROGRAM DRIVERGAUSS
      DIMENSION A(2,2),IR(2),IC(2)
      N = 2
      LDIM = 2
      
      DO I=1,N
        DO J=1,N
          A(J,I) = I*10+J
        END DO
      END DO
      CALL LU(A,LDIM,N,IR,IC)
      
      END PROGRAM DRIVERGAUSS