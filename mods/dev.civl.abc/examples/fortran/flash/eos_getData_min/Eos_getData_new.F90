subroutine Eos_getData(range,vecLen,solnData,gridDataStruct,eosData)

  implicit none
  
#include "Eos.h"
#include "Eos_map.h"
#include "constants.h"
#include "Flash.h"
  
  integer :: vecLen, gridDataStruct
  integer, dimension(LOW:HIGH,MDIM) :: range
  real, dimension(EOS_NUM*vecLen) :: eosData
  real, dimension(:,:,:,:) :: solnData

  integer :: i,j,k,n,m,pres,dens,gamc,temp,abar,zbar,eint,ekin,entr
  integer :: pres_map,dens_map,gamc_map,game_map,temp_map,entr_map
  integer :: eint_map,ener_map, velx_map, vely_map, velz_map, sumy_map, ye_map
  integer :: ib,ie,jb,je,kb,ke
  real :: kineticEnergy, internalEnergy
  
  integer :: dimension(1:EOSMAP_NUM_ROLES, 1:2, 1:5) :: eos_mapLookup
  real :: eos_eintSwitch, eos_smalle
  
  ib=range(LOW,IAXIS)
  jb=range(LOW,JAXIS)
  kb=range(LOW,KAXIS)
  ie=range(HIGH,IAXIS)
  je=range(HIGH,JAXIS)
  ke=range(HIGH,KAXIS)

  pres = (EOS_PRES-1)*vecLen
  dens = (EOS_DENS-1)*vecLen
  temp = (EOS_TEMP-1)*vecLen
  gamc = (EOS_GAMC-1)*vecLen
  eint = (EOS_EINT-1)*vecLen
  ekin = (EOS_EKIN-1)*vecLen
  abar = (EOS_ABAR-1)*vecLen
  zbar = (EOS_ZBAR-1)*vecLen
  entr = (EOS_ENTR-1)*vecLen

  pres_map = eos_mapLookup(EOSMAP_PRES,EOS_IN,gridDataStruct)
  dens_map = eos_mapLookup(EOSMAP_DENS,EOS_IN,gridDataStruct)
  temp_map = eos_mapLookup(EOSMAP_TEMP,EOS_IN,gridDataStruct)
  gamc_map = eos_mapLookup(EOSMAP_GAMC,EOS_IN,gridDataStruct)
  game_map = eos_mapLookup(EOSMAP_GAME,EOS_IN,gridDataStruct)
  eint_map = eos_mapLookup(EOSMAP_EINT,EOS_IN,gridDataStruct)
  ener_map = eos_mapLookup(EOSMAP_ENER,EOS_IN,gridDataStruct)
  velx_map = eos_mapLookup(EOSMAP_VELX,EOS_IN,gridDataStruct)
  vely_map = eos_mapLookup(EOSMAP_VELY,EOS_IN,gridDataStruct)
  velz_map = eos_mapLookup(EOSMAP_VELZ,EOS_IN,gridDataStruct)
  sumy_map = eos_mapLookup(EOSMAP_SUMY,EOS_IN,gridDataStruct)
  ye_map   = eos_mapLookup(EOSMAP_YE,  EOS_IN,gridDataStruct)
  entr_map = eos_mapLookup(EOSMAP_ENTR,EOS_IN,gridDataStruct)

  if(present(massFrac)) then
     m=1
     do k = kb,ke
        do j = jb,je
           do i = ib,ie
              do n = SPECIES_BEGIN,SPECIES_END
                 massFrac(m) = solnData(n,i,j,k)
                 m=m+1
              end do
           end do
        end do
     end do
  end if

  n = 0
  !! DEV: If / when we add a ptr dummy argument for passing in an offset, this will be n = ptr
  do k = kb,ke
     do j = jb,je
        do i = ib,ie
           call Eos_getData_loop1(vecLen, eosData, solnData, i,j,k,n, pres_map,dens_map,gamc_map,&
                    game_map,temp_map,entr_map,eint_map,ener_map, velx_map, vely_map, velz_map, &
                    sumy_map, ye_map, pres,dens,gamc,temp,abar,zbar,eint,ekin,entr)
        end do
     end do
  end do
  
  return
end subroutine Eos_getData 