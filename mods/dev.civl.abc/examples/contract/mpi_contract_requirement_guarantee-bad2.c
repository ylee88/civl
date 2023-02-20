#pragma CIVL ACSL

int nprocs;

/*@ requires \absentof \sendfrom(0 .. (nprocs-1), 0) \after \exit(0 .. nprocs-1) \until \exit(0);
  @ ensures  \absentof \exit \after \enter \until \enter(root);
  @*/
int gather(int root);
