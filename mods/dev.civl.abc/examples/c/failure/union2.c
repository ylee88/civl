struct _S {
  int val;
} s1;

union _U {
  struct _S s1;
} u1;

int main(){
  u1.s1.val = 1;
}

// Dupicated 's1' in line 3 and line 6 
// cause a null pointer exception
