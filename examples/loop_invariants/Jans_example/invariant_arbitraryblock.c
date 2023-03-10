#include <stdio.h>
#ifdef _CIVL
#include <civlc.cvh>
#endif

#ifdef _CIVL
$input int ni,nj;
$input int bi,bj;  //TODO: bounds of block sizes ?
$assume(ni > 1);
$assume(nj > 1);
$input double uin[ni][nj];
#endif
double u1[ni][nj];
double u2[ni][nj];

int main(int argc, char** argv) {
    int i,j,ir,jr,ib,jb;
    
    /*
    for(i=0;i<ni;i++) {
      for(j=0;j<nj;j++) {
        u1[i][j] = 0;
        u2[i][j] = 0;
      }
    }

    // straightforward loop nest
    for(i=1;i<ni-1;i++) {
      for(j=1;j<nj-1;j++) {
        u1[i][j] = uin[i][j]*uin[i][j];
      }
    }
    */
    $havoc(&u1);
    $havoc(&u2); 
    $assume($forall (int i : 0 .. ni-1) u1[i][0] == 0 &&  u1[i][nj-1] == 0 && u2[i][0] == 0 &&  u2[i][nj-1] == 0);
    $assume($forall (int j : 0 .. nj-1) u1[0][j] == 0 &&  u1[ni-1][j] == 0 && u2[0][j] == 0 &&  u2[ni-1][j] == 0);
    $assume($forall (int i : 1 .. ni-2) ($forall (int j : 1 .. nj-2) u1[i][j] == uin[i][j] * uin[i][j]));

    if (ni > 2) {
      i = ni - 1;	
      if (nj > 2)
	j = nj - 1;
      else
	j = 1;
    } else {
      i = 1;
      j = nj;
    }
    
    // blocked loop nest
    for(i=1;i<ni-bi;i=i+bi) {
      for(j=1;j<nj-bj;j=j+bj) {
        for(ib=0;ib<bi;ib++) {
          for(jb=0;jb<bj;jb++) {
            u2[i+ib][j+jb] = uin[i+ib][j+jb]*uin[i+ib][j+jb];
          }
        }
      }
    }
    // remainder i
    for(ir=i;ir<ni-1;ir=ir++) {
      for(jr=1;jr<nj-bj;jr=jr++) {
        for(jb=0;jb<bj;jb++) {
          u2[ir][jr+jb] = uin[ir][jr+jb]*uin[ir][jr+jb];
        }
      }
    }
    // remainder j
    for(jr=j;jr<nj-1;jr=jr++) {
      for(ir=1;ir<ni-bi;ir=ir++) {
        for(ib=0;ib<bi;ib++) {
          u2[ir+ib][jr] = uin[ir+ib][jr]*uin[ir+ib][jr];
        }
      }
    }
    // remainder ij
    for(ir=i;ir<ni-1;ir=ir++) {
      for(jr=j;jr<nj-1;jr=jr++) {
        u2[ir][jr] = uin[ir][jr]*uin[ir][jr];
      }
    }


    // assert equality of u1 and u2
    for(i=0;i<ni;i++) {
      for(j=0;j<nj;j++) {
        $assert(u1[i][j] == u2[i][j]);
      }
    }
}
