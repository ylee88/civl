all: driver.c c_function.c driver2.c f_function.f
	civl compare -spec driver.c c_function.c -impl driver2.c f_function.f 
clean: 
	rm -rf *~ CIVLREP