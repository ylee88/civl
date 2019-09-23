      SUBROUTINE MXM(A,N1,B,N2,C,N3)
      REAL A(N1,N2), B(N2,N3), C(N1,N3)

      DO J=1,N3
         DO I=1,N1
            C(I,J) = 0.0
            DO K=1, N2
               C(I,J) = C(I,J) + A(I,K)*B(K,J)
            ENDDO
         ENDDO
      ENDDO
      END
