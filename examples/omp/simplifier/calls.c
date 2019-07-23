int foo(int * a, int i) {
  a[i] += 1;
}

int main() {
  int a[10];

#pragma omp parallel for shared(a)
  for (int i = 0; i < 10; i++) {
    foo(a, i);
  }
}
