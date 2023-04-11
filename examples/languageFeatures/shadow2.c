#include <assert.h>
typedef struct teststr {
  int member;
} testStr;

int main() {
  int a = 0;
  for (int i = 0; i < 10; i++) {
    testStr i;
    i.member = 7;
    a += i.member;
  }
  assert(a==70);
}
