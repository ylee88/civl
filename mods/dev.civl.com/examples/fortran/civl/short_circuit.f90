program shortcircuiteval
  implicit none
  real :: res, arr(4)
  integer :: n, i
  n = 4
  do i=1,n
    arr(i) = i
  end do
  i = 6
  call shortcircuit(arr, n, i, res);
end program shortcircuiteval
       
subroutine shortcircuit(arr, arr_size, idx, relu)
  real :: arr(:)
  integer :: idx, arr_size
  real :: relu
  if(idx .le. arr_size .and. arr(idx) .ge. 0) then
    relu = arr(idx)
  else
    relu = 0
  end if
end subroutine shortcircuit