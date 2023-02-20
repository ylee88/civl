struct T {
  char s[100];
  char * p;
};

int main() {
  struct T t = {.s="hello", "string"};

  return (int)t.s[0];
}
