#pragma CIVL ACSL

/*@ requires (a^^b) && (b==>c) && (a <--> c) && (b --> c);
  @ ensures (a<==>b+c);
  @*/
void foo(int a, int b, int c);

int main(){
  foo(0, 0, 0);
}
