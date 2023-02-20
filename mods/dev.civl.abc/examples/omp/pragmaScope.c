// This example exposes the problem of have a pragma and a following statement in the same scope without brackets.
int main(){
  int i,j;
  int n=10, m=10;
  int a[n][m];

  for(i=0;i<n; i++)
    for(j=0;j<n; j++)
      a[i][j]= 0;

  for (i=0;i<n-1;i++)
    #pragma omp parallel for
    for (j=0;j<m-1;j++)
      a[i][j]=a[i+1][j+1];
}
