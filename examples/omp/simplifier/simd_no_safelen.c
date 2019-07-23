int main() {
  int a[10];
#pragma omp simd 
  for (int i = 0; i < 9; i++) {
    a[i] = i;
    a[i+1] = a[i] + 1;
  }
}
