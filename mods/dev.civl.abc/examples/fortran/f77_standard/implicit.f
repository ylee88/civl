      program p
      implicit integer (x-z), real (i,k-l)
      i = 2.5
      k = 2.5
      l = 2.5
      x = 2.5
      y = 2.5
      z = 2.5
      print *, i,k,l,x,y,z
c$ civl assert(i .eq. 2.5)
c$ civl assert(k .eq. 2.5)
c$ civl assert(l .eq. 2.5)
c$ civl assert(x .eq. 2)
c$ civl assert(y .eq. 2)
c$ civl assert(z .eq. 2)
      end program
