      SUBROUTINE RESIDUAL_8(N,IA1,IA2)
        INTEGER N,IA1(N),IA2(N)

        Do I=1,N
          IA2(I) = IA1(I) - (IA1(I) / 8)*8
        ENDDO
        RETURN
      END
