CIVL=civl-1.19
all: verify_rs_max verify_rs_sum

verify_rs_max:	driver.cvl  reduceScatter_optimized.h  reduceScatter_optimized.c
	$(CIVL) verify -input_mpi_nprocs=3 -inputNB=4 -D_CHECK_MAX driver.cvl reduceScatter_optimized.c

verify_rs_sum:	driver.cvl  reduceScatter_optimized.h  reduceScatter_optimized.c
	$(CIVL) verify -input_mpi_nprocs=5 -inputNB=8 driver.cvl reduceScatter_optimized.c

clean:
	rm -rf *~ CIVLREP