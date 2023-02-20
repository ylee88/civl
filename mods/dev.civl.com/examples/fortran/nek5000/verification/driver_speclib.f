      PROGRAM SPECLIB
      INTEGER NP = 3, I, J
      REAL TMP
      DIMENSION Z(3), W(3)
      
      DO I=1,NP
          Z(I) = I*2.0
          W(I) = 1.0
      END DO
      DO I=1, 2*NP, 2
          TMP = 2.0*I*ATAN(1)/NP
!$CVL $ASSUME(COS(TMP) + COS(4*ATAN(1) - TMP) == 0.0);
      END DO
!$CVL $ASSUME(2.0*COS(ATAN(1))*COS(ATAN(1)) == 1.0);
!$CVL $ASSUME(4.0*COS(10.0/3.0*ATAN(1))*COS(10.0/3.0*ATAN(1)) == 3.0)
      CALL ZWGL(Z,W,NP)
      END PROGRAM SPECLIB