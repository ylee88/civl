int x = 0;
int y = 0;
int main() {
  x = 7;
  if (x > 0) {
    x++;
  } else {
    if (y > 0) 
      x--;
    else 
      x -= 7;
  }
  x = 42;
}
