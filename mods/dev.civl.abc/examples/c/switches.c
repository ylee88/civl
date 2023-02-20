int x = 0;
int main() {
  switch(x) {
    case 0: 
            x++;
            // fall through
    case 1: x--; 
            x = 42;
            break;
    case 3: 
    case 5: 
    case 7: x = 0;
            break;
    default: x=42;
  }

  x = 1;

  switch(x+1) {}

  x = 2;
}
