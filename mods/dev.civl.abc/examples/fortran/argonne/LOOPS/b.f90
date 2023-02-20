subroutine f(n, a, b)
  integer :: n, i
  double precision :: a, b
  do i=0,n-1
    a = a+a*b + a*b*b
    if(.not. (mod(i,2) .eq. 0)) then
      a = a+b*a*a + b*a;
    end if
  end do
end subroutine
