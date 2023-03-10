program modulespec
  implicit none
!$CVL $input 
  integer :: inval
!$CVL $output
  integer :: outval
  integer :: n
!$CVL $assume(0 .LE. inval .AND. inval .LE. 10);
  n = 2
  outval = mod(inval, n)
end program modulespec
