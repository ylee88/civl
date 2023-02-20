      program test
      implicit none
      logical havetobreak
      integer i,j,jj,k,n

      n=10

      i=1
      do 13 k=1,n
        if(k .gt. 3) go to 14
        i=i+1
   13 continue
   14 continue

      j=1
      havetobreak = .false.
      do k=1,n
        if(k .gt. 3) then
          havetobreak = .true.
        end if
        if(.not. havetobreak) then
          j=j+1
        end if
      end do

      jj=1
      do k=1,n
        if(k .gt. 3) then
          exit
        end if
        jj=jj+1
      end do
      write(*,*) i, j, jj

      end program