C Hello World in Fortran
       program civl_assert
          logical b = .true.
          integer i = 1
           
          !$ civl $assert(b);    
          !$ civl $assert(i==1);  
       end
