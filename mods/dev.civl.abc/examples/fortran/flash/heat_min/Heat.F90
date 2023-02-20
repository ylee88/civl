program driver

  implicit none
  
  integer :: blockCount
  integer ::blockList(blockCount)
  real :: dt,time

!! Allocated in amr_initialize.F90 Line 282
  real :: unk(NVAR,NXB+2*NGUARD,NYB+2*NGUARD*K2D,NZB+2*NGUARD*K3D,MAXBLOCKS)
  
  

  call Heat(blockCount, blockList, dt, time)

end program 


subroutine Init()


#include "Flash.h"
#include "constants.h"

  implicit none

  integer :: pid
  real :: unk(:,:,:,:,:)
  
  integer :: gr_globalNumBlocks, alnblocks, io_meshNumProcs 
  integer :: xx, yy, procBlocks, io_meshMe, localNumBlocks
  integer :: gr_nToLeft(0:NPROC)
  
  integer :: mblk

  mblk = MAXBLOCKS
  gr_globalNumBlocks = NBLOCK
  io_meshNumProcs = NPROC
  io_meshMe = pid

!! src: source/IO/IOMain/hdf5/parallel/PM/io_readData.F90 #196--284

  !---------------------------------------------------------------------------
  ! compute the number of blocks on each processor -- this will be used to
  ! get the offset into the file for the parallel read
  !---------------------------------------------------------------------------

  
#ifndef _CIVL
  ! compute the approximate number of blocks per processor
  alnblocks = int(gr_globalNumBlocks/io_meshNumProcs) + 1
  
  ! check for error -- if the number of blocks we want to put on each
  ! processor is greater than maxblocks, then abort
  if (alnblocks .GT. MAXBLOCKS) then
     
     print *
     print *, '********** ERROR in READ_DATA ************'
     print *
     print *,' Number of blocks per processor exceeds maxblocks.'
     print *,' Suggest you reset maxblocks to a larger number or'
     print *,' run on a larger number of processors.'
     print *,' globalNumBlocks, io_meshNumProcs = ', gr_globalNumBlocks, io_meshNumProcs
     print *
     
     call Driver_abortFlash('[io_readData] ERROR: num blocks per proc exceeds maxblocks')
     
  end if
  
#else

  alnblocks = gr_globalNumBlocks/io_meshNumProcs + 1

!$ CIVL $assert(alnblocks <= mblk)

#endif
!! end ifndef _CIVL (the 1st one)
  
  ! figure out the excess blocks
  yy = (io_meshNumProcs*alnblocks) - gr_globalNumBlocks
  xx = io_meshNumProcs - yy
  
  ! loop over all the processor numbers and figure out how many blocks are
  ! stored to the left of the processor -- this is a little tricky
  
  gr_nToLeft(0) = 0
  
  do i = 0, io_meshNumProcs - 2
     if (i .LT. xx) then
        procBlocks = alnblocks
     else
        procBlocks = alnblocks - 1
     endif
     
     if (alnblocks .EQ. 0) then
        if (i .LT. gr_globalNumBlocks) then
           procBlocks = 1
        else
           procBlocks = 0
        end if
     end if
     
     ! we have the number of blocks on proc i, the number of blocks on i+1 is
     ! the number of blocks on i + the number of blocks left of i
     if (i .EQ. 0) then
        gr_nToLeft(i+1) = procBlocks
     else
        gr_nToLeft(i+1) = procBlocks + gr_nToLeft(i)
     endif
  enddo
  
  ! figure out how many blocks are on the current proc.
  if (io_meshMe < xx) then
     localNumBlocks = alnblocks
  else
     localNumBlocks = alnblocks - 1
  endif
  
  if (alnblocks .EQ. 0) then
     if (io_meshMe < gr_globalNumBlocks) then
        localNumBlocks = 1
     else
        localNumBlocks = 0
     end if
  end if
  
  ! compute the offset into the dataspace in the HDF5 file
  gr_globalOffset = gr_nToLeft(io_meshMe)
  
#ifndef _CIVL

  call Grid_putLocalNumBlks(localNumBlocks)

  !find our offset into a potentially split file:
  if(io_outputSplitNum > 1) then
     call MPI_ALLREDUCE(gr_globalOffset, splitOffset,1,FLASH_INTEGER,&
                        MPI_MIN, io_comm, ierr)
     localOffset = gr_globalOffset - splitOffset
     
  else
     localOffset = gr_globalOffset
  end if
  
#else
  
     localOffset = gr_globalOffset

#endif
!! end ifndef _CIVL (the 2nd one)

end subroutine Init


subroutine Heat(blockCount, blockList, dt, time)

#include "Flash.h"
#include "constants.h"

  implicit none
  
  integer :: blockCount
  integer ::blockList(blockCount)
  real :: dt,time
  
  real, pointer :: solnData(:,:,:,:)
  integer :: blockID, i,j,k,n

  !$omp parallel default(shared) private(n,blockID,k,j,i,solnData)
  !$omp do 
  do n = 1, blockCount
     blockID = blockList(n)

     solnData => unk(:,:,:,:,blockID)
     
     do k = 1, 1
        do j = 1, 1
           do i = NGUARD + 1, NGUARD + NXB
              
              solnData(1,i,j,k) = solnData(1,i,j,k) + 10.0*dt

           enddo
        enddo
     enddo

     nullify(solndata)

  enddo
  !$omp end do
  !$omp end parallel

end subroutine Heat
