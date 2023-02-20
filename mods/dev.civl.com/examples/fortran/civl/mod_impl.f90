program modulespec
  implicit none
!$CVL $input 
  integer :: inval
!$CVL $output
  integer :: outval
  integer :: i, n ,m
!$CVL $assume(0 .LE. inval .AND. inval .LE. 10);
  m = inval
  n = inval/2
  do i=1,n
    m = m - 2
  end do
  outval = m
end program modulespec
