!! Modified from FLASH application: 
!! SrcInfo: ./source/physics/sourceTerms/Heat/HeatMain/Neutrino/Heat.F90

#include "constants.h"
#include "Flash.h"

program HeatMin
  implicit none
  
  integer :: blkCtr
  integer :: blkLst(MAXBLOCKS)
  real :: data(:,:,:,:,MAXBLOCKS), dt, time

  blkCtr = 1
  blkLst = 1
  
  time = 0.0
  dt = 0.5
  
  data = 0.0
  data(2,:,:,:,:) = 1.0
  
  call Heat(blkCtr, blkLst, dt, time)

  ! assert

end program HeatMin


subroutine Grid_getBlkIndexLimits(blockId, blkLimits, blkLimitsGC)
 
  implicit none

  integer,intent(IN) :: blockId
  integer, dimension(2,MDIM), intent(OUT) :: blkLimits,blkLimitsGC

  blkLimitsGC = 1
  blkLimitsGC(HIGH,IAXIS) = GRID_IHI_GC
  blkLimitsGC(HIGH,JAXIS) = GRID_JHI_GC
  blkLimitsGC(HIGH,KAXIS) = GRID_KHI_GC
  
  blkLimits(LOW,IAXIS) = GRID_ILO
  blkLimits(HIGH,IAXIS) = GRID_IHI
  blkLimits(LOW,JAXIS) = GRID_JLO
  blkLimits(HIGH,JAXIS) = GRID_JHI
  blkLimits(LOW,KAXIS) = GRID_KLO
  blkLimits(HIGH,KAXIS) = GRID_KHI
  
end subroutine Grid_getBlkIndexLimits



subroutine Heat(blockCount, blockList, dt, time)
!! real,dimension(:,:,:,:,MAXBLK) :: data

  use civl_data, ONLY : data

  implicit none
  
  integer :: blockCount
  integer ::blockList(blockCount)
  real :: dt,time
  
  real,dimension(:,:,:,:) :: sdata
  integer :: blockID
  integer :: i,j,k,n
  
  
  !$omp parallel &
  !$omp private(n,blockID,k,j,i,sdata) &
  !$omp shared(blockCount,blockList,dt,time,data)
  
  
  !$omp do 
  do n = 1, blockCount
     blockID = blockList(n)
     sdata = data(:,:,:,:,blockID)

     do k = 1, 1
        do j = 1, 1
           do i = 1, NGUARD + NXIDX/NPROC
                           
              data(2,i,j,k,blockID) = data(2,i,j,k,blockID) + dt
              
              data(1,i,j,k,blockID) = data(2,i,j,k,blockID) + dt

           enddo
        enddo
     enddo

  enddo
  !$omp enddo
  !$omp end parallel

  return
end subroutine Heat