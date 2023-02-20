/* ACSL annotations will be taken as regular commments if there is no
 * '#pragma CIVL ACSL' in the translation unit.
 */

int main() {
  int i = 0;
  //@ asset i == 1;
  /*@ assert i == 0; */
}
