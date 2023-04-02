#include <assert.h>
union Node {
  union {
    int a;
    union {
      int b;
      union {
	int c;
	int d;
      };
    };
  };
};
int main() {
  union Node node = { .d=1 };
  node.b; // error
}
