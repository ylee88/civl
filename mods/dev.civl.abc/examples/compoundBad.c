int main() {
  int n = 10;
  
  struct T {
    int a[n];
    int x;
  };

  struct T t = {0};

  return t.x;
}
