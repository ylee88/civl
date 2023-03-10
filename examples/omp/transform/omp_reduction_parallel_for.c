#include <assert.h>
#include <omp.h>
#include <stdio.h>

int main () {
    int n = 5;
    int arr[5] = {5,3,9,1,7};
    // Reduction combiners
    int max = -10;
    int min = 10;
    int sum = 0;
    int sub = 30;
    int prod = 1;
    _Bool land = 1; /* TRUE */
    int band = ~(arr[0] & 0);
    _Bool lor = 0; /* FALSE */
    int bor = (arr[0] & 0);
    int bxor = (arr[0] & 0);

    
#pragma omp parallel for reduction(+:sum)
    for (int i=0; i<n; i++)
        sum = sum + arr[i];
    assert(sum == 25);

}
