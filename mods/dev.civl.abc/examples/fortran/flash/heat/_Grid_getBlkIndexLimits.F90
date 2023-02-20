subroutine Grid_getBlkIndexLimits(blockId, blkLimits, blkLimitsGC)

  implicit none

  integer,intent(IN) :: blockId
  integer,dimension(2,MDIM),intent(OUT) :: blkLimits,blkLimitsGC

  blkLimits(1,IAXIS) = 1
  blkLimits(HIGH,IAXIS) = NXB + CONSTANT_TWO*NGUARD*K1D
  blkLimits(1,JAXIS) = 1
  blkLimits(HIGH,JAXIS) = NYB + CONSTANT_TWO*NGUARD*K1D
  blkLimits(1,KAXIS) = 1
  blkLimits(HIGH,KAXIS) = NZB + CONSTANT_TWO*NGUARD*K1D

  blkLimitsGC(LOW,IAXIS) = 1
  blkLimitsGC(HIGH,IAXIS) = 1 + 2*NGUARD + NIDX/NPROC -1
  blkLimitsGC(LOW,JAXIS) = 1
  blkLimitsGC(HIGH,JAXIS) = 1
  blkLimitsGC(LOW,KAXIS) = 1
  blkLimitsGC(HIGH,KAXIS) = 1

end subroutine Grid_getBlkIndexLimits
