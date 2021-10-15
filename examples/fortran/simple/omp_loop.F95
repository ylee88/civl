program OMPLOOP
    use omp_lib
    integer :: i
    integer :: a(5)

!$omp parallel do
    do i = 1, 5
        a(i) = i
    end do
!$omp end parallel do
end program