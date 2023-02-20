/* Translated by f2c (version 20100827).
 * You must link the resulting object file with libf2c 
 */

#include "f2c.h"

/* Table of constant values */

static integer c__9 = 9;
static integer c__1 = 1;

/* Hello World in Fortran */
/* Main program */ int MAIN__(void)
{
    /* Builtin functions */
    integer s_wsle(cilist *), do_lio(integer *, integer *, char *, ftnlen), 
	    e_wsle(void);

    /* Fortran I/O blocks */
    static cilist io___1 = { 0, 6, 0, 0, 0 };


    s_wsle(&io___1);
    do_lio(&c__9, &c__1, "Hello World!", (ftnlen)12);
    e_wsle();
    return 0;
} /* MAIN__ */

/* Main program alias */ int hw_ () { MAIN__ (); return 0; }
