all: compare

compare: adder_spec.c adder_par.c
	civl compare -loop -spec -loop adder_spec.c -impl -loop -input_mpi_nprocs_hi=3 adder_par.c

clean:
	rm -rf *~ CIVLREP SARL_Why3