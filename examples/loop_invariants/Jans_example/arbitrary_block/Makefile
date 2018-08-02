CIVL=civl
VERIFY=$(CIVL) verify -loop

all:  ml_inner_three  ml_all_four

ml_inner_three: inner_three.cvl	
	$(VERIFY) $<



ml_all_four: all_four.cvl
	$(VERIFY) $<

clean:
	rm -rf *~ .sarl	CIVLREP


