      subroutine f(n, a, b)
      integer n, i
      double precision a, b
      i = 0
      do 100
        if(i .ge. n) go to 200
        a = a+a*b + a*b*b - 1
        if(mod(i,2) .eq. 0) go to 100
        a = a+b*a*a + b*a - 1;
  100 i = i+1
  200 continue
      end subroutine