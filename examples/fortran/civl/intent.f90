program intent_arg
  implicit none
!$CVL $input
  integer max
  integer n, i, k
!$CVL $assume(0 .LE. max .AND. max .LE. 10);
  n = 0
  do i=1,max 
    call incr(n)
  end do
!$CVL $assert(n .EQ. 0);
end program intent_arg

subroutine incr(i)
  integer, intent(out) :: i
  i = -1;
  i = i + 1
end subroutine incr
