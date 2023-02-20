#pragma CIVL ACSL

int nprocs;

/*@ requires \absentof \sendfrom(0 .. (nprocs-1), 0) \after \exit(0 .. nprocs-1) \until \exit;
  @ ensures  \absentof \exit \after \enter \until \enter(root);
  @ ensures  \absentof \sendfrom(0 .. (nprocs-1), 0) \after \exit(0 .. nprocs-1) \until \exit;
  @*/
int gather(int root);
