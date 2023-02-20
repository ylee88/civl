#ifndef NBLK
#define NBLK 10
#endif 

subroutine HeatMin(io_meshMe)

#include "Flash.h"
#include "constants.h"

  implicit none
  
  integer :: mblk = MAXBLOCKS

  real :: data(1,NXB,NYB,NZB,NBLK)
  
  integer :: gr_globalNumBlocks, gr_globalOffset
  integer :: gr_nToLeft(0:NPROC)
  integer :: io_meshNumProcs, io_meshMe
  integer :: localNumBlocks, localOffset
  integer :: alnblocks, xx, yy, procBlocks
  
  integer :: nxb, nyb, nzb
  integer :: unk(1,NXB,NYB,NZB,MAXBLOCKS)


  gr_globalNumBlocks = NBLOCK
  io_meshNumProcs = NPROC

!!!! src: source/IO/IOMain/hdf5/parallel/PM/io_readData.F90 #196--284

  ! compute the approximate number of blocks per processor
  alnblocks = gr_globalNumBlocks/io_meshNumProcs + 1

!$ CIVL $assume(alnblocks <= mblk)

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
  
  localOffset = gr_globalOffset
  
!!!! Simulation H5Sselect_hyperslab in mode H5S_SELECT_SET
  do iblk=1,nxb 
    do 
  end do
  unk(1,:,:,:,1:1+localNumBlocks) = data(1,:,:,:,localOffset:localOffset+localNumBlocks)
  
end subroutine