CIVL=civl
VRF=verify
SP=-showProgram
DB=-DBYE=1
DERR=-DERR=1
DNM=-DNUM_MSG=
DN=-DNAME=
NAME=\"RAPIDS\"
SRC=preproc_all.f

SHOW=$(CIVL) $(VRF) $(SP)

msg0: $(SRC)
	$(SHOW) $(DB) $(DNM)0 $(SRC)

msg1: $(SRC)
	$(SHOW) $(DB) $(DNM)1 $(SRC)

msg2: $(SRC)
	$(SHOW) $(DB) $(DNM)2 $(SRC)

msg3: $(SRC)
	$(SHOW) $(DB) $(DNM)3 $(SRC)

nobye: $(SRC)
	$(SHOW) $(DNM)3 $(SRC)

rapids: $(SRC)
	$(SHOW) $(DB) $(DNM)3 $(DN)$(NAME) $(SRC)

err: $(SRC)
	$(SHOW) $(DERR) $(SRC)
clean:
	rm -rf CIVLREP *~
