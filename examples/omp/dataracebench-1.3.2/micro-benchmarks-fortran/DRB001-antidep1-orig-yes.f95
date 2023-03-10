!!!~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~!!!
!!! Copyright (c) 2017-20, Lawrence Livermore National Security, LLC
!!! and DataRaceBench project contributors. See the DataRaceBench/COPYRIGHT file for details.
!!!
!!! SPDX-License-Identifier: (BSD-3-Clause)
!!!~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~!!!

!A loop with loop-carried anti-dependence.
!Data race pair: a[i+1]@36:9 vs. a[i]@36:16

!!- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -!!
!! NOTE by WenhaoWu (wuwenhao@udel.edu)                                  !!
!! This example is modified so that the bound can be adjusted            !!
!! by defining 'N' and if it is not defined then the bound value         !!
!! in the original example is used as the default value of 'N'           !!
!!- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -	- -	-!!

#ifndef N
#define N 10
#endif 

program DRB001_antidep1_orig_yes
use omp_lib
    implicit none
    integer :: i, len
    integer :: a(N)

    len = N

    do i = 1, len
        a(i) = i
    end do

    !$omp parallel do
    do i = 1, len-1
        a(i) = a(i+1) + 1
    end do
    !$omp end parallel do

    100 format ('a(N/2)=',i3)
    print 100, a(N/2)
end program
