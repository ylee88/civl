      subroutine mxm(a,n1,b,n2,c,n3)
      real a(n1,3),b(3,n3),c(n1,n3)
      do j=1,n3
         do i=1,n1
            c(i,j) = a(i,1)*b(1,j) + a(i,2)*b(2,j) + a(i,3)*b(3,j)
         enddo
      enddo
      return
      end
