CIVL=civl
VERIFY=$(CIVL) verify -loop

all:  main lemmas

main: arbitrary_block.cvl
	$(VERIFY) $<

lemmas: lemmas.cvl
	$(VERIFY) $<


clean:
	rm -rf *~ .sarl	CIVLREP


