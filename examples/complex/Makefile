
all:


verify_complex_basics: complex_basics.c
	civl verify $<

run_complex_basics: complex_basics.c
	cc -o complex_basics.exec $<
	./complex_basics.exec


clean:
	rm -rf *.exec *~ CIVLREP a.out

_PHONY: verify_complex_basics run_complex_basics clean all
