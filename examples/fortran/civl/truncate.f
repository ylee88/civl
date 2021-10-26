      PROGRAM TRUNCATE
        IMPLICIT NONE
        INTEGER extremelylongvariablename, extremelylongvariablename1
        INTEGER sum
        extremelylongvariablename = 0
        extremelylongvariablename1 = 1
        sum = 10 + extremelylongvariablename + extremelylongvariablename1
        !$CVL $assert(sum .EQ. 11);
      END PROGRAM TRUNCATE
