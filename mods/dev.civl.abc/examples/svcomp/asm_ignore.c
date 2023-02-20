
int main() {
  asm volatile ("" "xchg" "q %q0, %1\n" :
                "+r" (__ret), "+m" (*((&head->first))) : :
                "memory", "cc");
  asm volatile goto (blah blah blah);
  asm goto (blah);
}
