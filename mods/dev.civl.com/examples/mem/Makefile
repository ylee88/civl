CIVL=civl
VERIFY=$(CIVL) verify

all: mem_complex

mem_complex: mem_complex.cvl
	$(VERIFY) $<

clean:
	rm -rf CIVLREP *~