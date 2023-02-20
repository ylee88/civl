       PROGRAM MXMDRIVER

       INTEGER N
       PARAMETER (N=4)
       REAL A(N,N), B(N,N), C(N,N)
       INTEGER I,J

       DO J=1,N
         DO I=1,N
           A(I,J) = 1.0*(I+J)
           B(I,J) = 1.0*(I-J)
         ENDDO
       ENDDO

C       DO I=1,N
C         DO J=1,N
C           C(I,J) = 0.0
C           DO K=1,N
C             C(I,J) = C(I,J) + A(I,K)*B(K,J)
C           ENDDO
C         ENDDO
C       ENDDO

       CALL MXM(A,N,B,N,C,N)

       DO J=1,N
         DO I=1,N
            PRINT *,"C(",I,",",J,") = ",C(I,J)
         ENDDO
       ENDDO

       END
