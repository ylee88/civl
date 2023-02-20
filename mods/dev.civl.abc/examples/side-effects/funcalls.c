int f(int a){
  return a;
}

$system $state_f int g(int a);

$system[funcalls] $pure int h(int b);

$abstract int k(int t);

int main(){
  int t;
  t=f(1)+f(2);
  t=g(1)+g(2);
  t=k(1)*k(g(2));
  t=k(3)+f(9);
  t=k(h(t));
  t=k(5)+k(f(6));
}
