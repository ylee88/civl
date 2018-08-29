#include<civlc.cvh>
#include<stdio.h>

void RESIDUAL_8(int* N, int ia1[], int ia2[]);

$input int X;
$assume(0 < X);

void main(){
  int a[1], b[1];
  int size = 1;

    a[0] = X;
    b[0] = 0;

  RESIDUAL_8(&size, a, b);

  
  //for (int i = 0; i < 1; i++){
  //    printf("%s%d%s%d", "B[", i, "] = ", b[i]);
  //}
}

