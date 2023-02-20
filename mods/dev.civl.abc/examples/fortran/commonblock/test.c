union __FCB__CB_MC {
  struct __FCB__PROG_MAIN_CB_MC {
    float R;
    int A;
    float F;
  } prog_main;

  struct __FCB__SUBR_SUB_CB_MC {
    float A;
    int I;
    float B;
  } subr_sub;
} comm_blk_mc;

void sub(float P, float Q) {
  comm_blk_mc.subr_sub.B = comm_blk_mc.subr_sub.I + 2*comm_blk_mc.subr_sub.A;
}

void main () {
  float X, Y;
  comm_blk_mc.prog_main.A = 5;
  comm_blk_mc.prog_main.R = 3.5;
  sub(X, Y);
  printf("%d", comm_blk_mc.F);
}