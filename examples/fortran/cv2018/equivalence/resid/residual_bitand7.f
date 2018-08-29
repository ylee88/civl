      SUBROUTINE RESIDUAL_8(N,IA1,IA2)
        INTEGER N,IA1(N),IA2(N)

        Do I=1,N
          IA2(I) = IAND(IA1(I), 7)
        ENDDO
        RETURN
      END
