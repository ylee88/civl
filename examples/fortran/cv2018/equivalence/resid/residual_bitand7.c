void RESIDUAL_8(int* N, int  IA1[], int  IA2[]) {
    int I;
    for (I = 1; I <= *N; I = I + 1) {
        IA2[I - 1] = IA1[I - 1] & 7;
    }
    return;
}
