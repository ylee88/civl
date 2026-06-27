#include <civlc.cvh>
#include <stdlib.h>

$input int sel;

int main(void) {
    $assume(sel == 0 || sel == 1);
    double *p[2];
    p[0] = malloc(sizeof(double));
    p[1] = malloc(sizeof(double));
    p[sel][0] = 1.0;
    free(p[0]);
    free(p[1]);
}
