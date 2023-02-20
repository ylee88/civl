C Hello World in Fortran
       program hello
          logical b = .true.
          integer i = 1
          print *, "Hello World!"
!$CVL $assert(i==1);   
!$CVl $assert(b);    
       end
