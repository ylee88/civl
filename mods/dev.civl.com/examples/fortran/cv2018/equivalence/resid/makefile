all: residual_driver.c residual_bitand7.c residual_bitand7.f residual_mod8.c residual_intdiv.f
	civl compare -spec residual_driver.c residual_bitand7.c -impl residual_driver.c residual_bitand7.f 
	civl compare -spec residual_driver.c residual_mod8.c -impl residual_driver.c residual_bitand7.f 
	civl compare -spec residual_driver.c residual_bitand7.c -impl residual_driver.c residual_intdiv.f 
	civl compare -spec residual_driver.c residual_mod8.c -impl residual_driver.c residual_intdiv.f  
clean: 
	rm -rf *~ CIVLREP