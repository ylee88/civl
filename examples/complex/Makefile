
all: verify run

verify: verify_complex_basics verify_complex1 verify_complex_funs \
   verify_mpi_complex

run: run_complex_basics run_complex_funs


verify_complex_basics: complex_basics.c
	civl verify $<

run_complex_basics: complex_basics.c
	cc -Wall -o complex_basics.exec $<
	./complex_basics.exec

verify_complex1: complex1.cvl
	civl verify $<

verify_complex_funs: complex_funs.c
	civl verify $<

run_complex_funs: complex_funs.c
	cc -Wall -o complex_funs.exec $<
	./complex_funs.exec

verify_mpi_complex: mpi_complex.cvl
	civl verify -input_mpi_nprocs=2 $<

clean:
	rm -rf *.exec *~ CIVLREP a.out

_PHONY: verify_complex_basics run_complex_basics \
   verify_complex1 \
   verify_complex_funs run_complex_funs \
   verify_mpi_complex \
   all verify run clean
