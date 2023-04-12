#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
struct S {
  struct S * fld;
};
int main() {
  struct S *p = malloc(sizeof(struct S));
  p->fld = p;
  p = NULL;
  // memory leak should be reported now
  while (1) { printf("hi"); }
}
