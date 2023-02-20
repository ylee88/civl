subroutine Grid_getBlkPtr(blockID,dataPtr)

#include "constants.h"
#include "Flash.h"

  use physicaldata, ONLY : unk
  
  implicit none
  integer, intent(in) :: blockID
  real, dimension(:,:,:,:), pointer :: dataPtr
  
  dataPtr => unk(:,:,:,:,blockid)


  return
end subroutine Grid_getBlkPtr








