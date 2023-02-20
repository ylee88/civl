!! Modified from FLASH application: 
!! SrcInfo: ./source/physics/sourceTerms/Heat/HeatMain/Neutrino/Heat.F90

subroutine Heat(blockCount, blockList, dt, time)
!! real,dimension(:,:,:,:,MAXBLK) :: data

  use civl_data, ONLY : data

  implicit none
  
  integer,intent(IN) :: blockCount
  integer,dimension(blockCount),intent(IN)::blockList
  real,intent(IN) :: dt,time
  
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
              
              data(1,i,j,k,blockID) = data(2,i,j,k,blockID) - dt

           enddo
        enddo
     enddo

  enddo
  !$omp enddo
  !$omp end parallel

  return
end subroutine Heat