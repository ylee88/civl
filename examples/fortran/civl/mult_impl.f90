program powspec
  implicit none
!$CVL $input 
  integer :: inval0, inval1
!$CVL $output
  integer :: outval
  integer :: i, res
!$CVL $assume(0.0 .LE. inval0 .AND. inval0 .LE. 10.0);
!$CVL $assume(1.0 .LE. inval1 .AND. inval1 .LE. 3.0);
  res = inval0
  do i=1,inval1-1
    res = res + inval0
  end do
  outval = res
end program powspec
