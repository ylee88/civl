      PROGRAM MXM_NAIVE
      
      INTEGER N
      PARAMETER (N=4)
      real A(N,N),B(N,N),C(N,N)
      INTEGER I,J
      
      DO J=1,N
        DO I=1,N
          A(I,J) = 1.0*(I+J)
          B(I,J) = 1.0*(I-J)
        ENDDO
      ENDDO
      DO I=1,N
        DO J=1,N
          C(I,J) = 0.0
          DO K=1,N
            C(I,J) = C(I,J) + A(I,K)*B(K,J)
          ENDDO
        ENDDO
      ENDDO

      DO J=1,N
        DO I=1,N
           PRINT *,"C(",I,",",J,") = ",C(I,J)
        ENDDO
      ENDDO
      END 
