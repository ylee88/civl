#include <assert.h>
struct Node {
  int id;
  union {
    int f1;
    double f2;
  };
  union {
    int f3;
    float f4;
  };
};
int main() {
  struct Node node = { .id=333, .f1=444, .f2=3.14 };
  assert(node.id==333);
  assert(node.f2==3.14);
  assert(node.f3==0);
}
