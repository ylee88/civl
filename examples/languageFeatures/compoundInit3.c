#include <assert.h>
union Node {
  int x, y;
};
int main() {
  union Node node = { .y=1 };
  node.x; // error
}
