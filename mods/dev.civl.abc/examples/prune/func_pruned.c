#pragma CIVL ACSL
//========================= func.c =========================
int x;
/*@ reads x;*/
$system[func] int foo();
int main()
{
  foo();
}
