Program Driver

implicit none

#include "Eos.h"
#include "Eos_map.h"
#include "constants.h"
#include "Flash.h"

  integer, dimension(LOW:HIGH,MDIM) :: range
  integer :: vecLen, i, j, k, l, n, dataStruct
  real, dimension(EOS_NUM,NZB,NYB,NXB) :: solnData
  real, dimension(EOS_NUM*NZB*NYB*NXB) :: eosData
  
  range(LOW, IAXIS) = GRID_ILO
  range(LOW, JAXIS) = GRID_JLO
  range(LOW, KAXIS) = GRID_KLO
  range(HIGH, IAXIS) = GRID_IHI
  range(HIGH, JAXIS) = GRID_JHI
  range(HIGH, KAXIS) = GRID_KHI
  
  n = 1
  dataStruct = 1
  do l = 1, EOS_NUM
    do k = GRID_KLO,GRID_KHI
      do j = GRID_JLO,GRID_JHI
        do i = GRID_ILO,GRID_IHI
          solnData(l,k,j,i) = 1.0
          eosData(n) = 0.0
          n = n+1
        end do
      end do
    end do
  end do
 
  call Eos_getData(range,vecLen,solnData,dataStruct,eosData)

End Program