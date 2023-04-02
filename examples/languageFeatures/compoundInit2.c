#include <assert.h>
struct Node {
  int x, y;
};
int main() {
  struct Node node = { 1 };
  assert(node.x==1);
  assert(node.y==0);
}
