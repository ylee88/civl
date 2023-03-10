! @expect verified

program main
  implicit none
  integer, pointer :: box
  integer, target :: x = 3
  box => x
  !print *, box == 3
  !$CVL $assert(box == 3)
end program main
