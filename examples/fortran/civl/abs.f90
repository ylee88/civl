       program abs
         implicit none
!$CVL $input
  		 integer :: arg
         integer :: x
!$CVL $assume(-10 .LE. arg .AND. arg .LE. 10);
         x = ABS(arg)
!$CVL $assert(0 .LE. x);
       end program abs
