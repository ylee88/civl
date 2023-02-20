#define MSG0 "Hello World!"
#define MSG1 "Bye World!"
      program hello
      print *,MSG0
#ifdef BYE
      print *,MSG1
#endif
      end
