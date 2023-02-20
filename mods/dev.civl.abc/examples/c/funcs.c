int f(int a, int b);

int f(int c, int d);

/*@ executes_when k<g;
  @ requires k>0;
  @ ensures \result<1;
  @*/
int f(int k, int g){
  return 0;
}

void main(){
  f(0,0);
}
