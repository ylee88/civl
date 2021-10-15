      subroutine mxm(a,n1,b,n2,c,n3)
      real a(n1,n2),b(n2,n3),c(n1,n3)
      do j=1,n3
         do i=1,n1
           c(i,j) = 0.0
           do k = 1,n2
            c(i,j) = c(i,j) + a(i,k)*b(k,j)
           end do
         enddo
      enddo
      return
      end
