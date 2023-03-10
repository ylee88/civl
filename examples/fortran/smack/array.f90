! @expect verified

program main
  implicit none
  integer, dimension(1) :: array = (/ 0 /)
  !print *, array(1) == 3
  array(1) = 3
  !$CVL $assert(array(1) == 3)
end program main
