      PROGRAM MXMDRIVER
      IMPLICIT NONE
      !$CVL $input
      integer BOUND
      !$CVL $output
      integer OUTVAL
      
      INTEGER N = BOUND
      real RES
      real A(N,N),B(N,N),C(N,N)
      INTEGER I,J
      !$CVL $assume(BOUND .EQ. 2);
      RES = 0.0
      DO J=1,N
        DO I=1,N
          A(I,J) = 1.0*(I*N+J)
          B(I,J) = 1.0*(I-J*N)
        ENDDO
      ENDDO
      CALL MXM(A, N, B, N, C, N)
      DO J=1,N
        DO I=1,N
          RES = RES + C(I,J)
        ENDDO
      ENDDO
      PRINT *, RES
      OUTVAL = RES
      END 
