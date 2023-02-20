int x = 0;
int main() {
  while (x<10) {
    switch(x) {
    case 1: while (x>5) {
              x--;
              if (x == 2) break;
            }
            continue;
    case 2: while (x<7) {
              x++;
              if (x == 3) continue;
            }
            break;
    default: x=42;
    }
    x = 17;
  }
}
