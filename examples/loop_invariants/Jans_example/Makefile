CIVL=civl
OPTION=-loop
VERIFY=$(CIVL) verify $(OPTION) 

all : $(patsubst %.cvl, verify_%, $(wildcard *))

verify_%:  %.cvl
	$(VERIFY) $<

clean:
	rm -rf *~ CIVLREP SARL_Why3