union _U {
  struct _S {
    int val;
  } s1;
} u1;

int main(){
  u1.s1.val = 1;
}

//A dereference violation is reported for line 8
