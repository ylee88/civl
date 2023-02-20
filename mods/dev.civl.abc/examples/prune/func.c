#pragma CIVL ACSL 

int x;

/*@ reads x; */
$system int foo();

int main(){
  foo();
}
