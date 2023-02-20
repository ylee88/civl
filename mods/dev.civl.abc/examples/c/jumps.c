int x = 0;
int y = 0;

void main() {

  while (x<10) {
    x++;
    while (y<20) {
      if (x+y==1) return;
      y++;
    }
  }

  x = 1;

  while (x<30) {
    x--;
    if (x==2) break;
  }

  x = 2;

  while (x<40) {
    x--;
    if (x==3) {
       x = 3;
       continue;
    }
  }

  x = 4;
 
  goto exitpoint;

  x = 42;
  x = 42*42; 
   
  exitpoint: x = 5; 
}
