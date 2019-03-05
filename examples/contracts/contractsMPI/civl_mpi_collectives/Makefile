allgather: allgather.c
	contract verify -mpiContract=allgather -input_mpi_nprocs=5 -collectSymbolicConstants allgather.c

allgather_bad_ensures: allgather-bad_ensures.c
	contract verify -mpiContract=allgather -input_mpi_nprocs=5 -collectSymbolicConstants allgather-bad_ensures.c

allgather_bad_requires: allgather-bad_ensures.c
	contract verify -mpiContract=allgather -input_mpi_nprocs=5 -collectSymbolicConstants allgather-bad_datasize.c

broadcast: broadcast.c
	contract verify -mpiContract=broadcast -input_mpi_nprocs=5 -collectSymbolicConstants broadcast.c

broadcast_bad_ensures: broadcast-bad_ensures.c
	contract verify -mpiContract=broadcast -input_mpi_nprocs=5 -collectSymbolicConstants broadcast-bad_ensures.c

gather: allgather.c
	contract verify -mpiContract=gather -input_mpi_nprocs=5 -collectSymbolicConstants allgather.c

gather_bad_impl: gather-bad_impl.c
	contract verify -mpiContract=gather -input_mpi_nprocs=5 -collectSymbolicConstants gather-bad_impl.c

reduce_int_sum: reduce_int_sum.c
	contract verify -mpiContract=reduce_sum -input_mpi_nprocs=5 -collectSymbolicConstants reduce_int_sum.c

scatter: scatter.c
	contract verify -mpiContract=scatter -input_mpi_nprocs=5 -collectSymbolicConstants scatter.c

scatter_bad_impl: scatter.c
	contract verify -mpiContract=scatter -input_mpi_nprocs=5 -collectSymbolicConstants scatter-bad_inplace.c

scatter_bad_ensures: scatter.c
	contract verify -mpiContract=scatter -input_mpi_nprocs=5 -collectSymbolicConstants scatter-bad_ensures.c

clean:
	rm -rf *~ CIVLREP SARL_Why3