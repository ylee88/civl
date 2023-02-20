CIVL=civl
VERIFY=${CIVL} verify

all: array barrier

array: arrayaccess.cvl
	${VERIFY} $<

barrier: barrier.cvl
	${VERIFY} $<

clean:
	rm -rf CIVLREP/*

