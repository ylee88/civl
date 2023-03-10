CIVL=civl
VERIFY=$(CIVL) verify


all: assignments  assignmentsNoSimplify

assignments: assignments.cvl
	$(VERIFY) -DSIMPLIFY $<

assignmentsNoSimplify: assignments.cvl
	$(VERIFY) -simplify=false $<

clean:
	rm -rf *~ CIVLREP .sarl

