int a, b, c;
int *p, *q;

int main() {
  int la, lb, lc;
  int *lp, *lq;
  int **lpp, **lqq;

  lp = &la;
  lp = &lb;
  lq = lp;
  lq = &lc;
  lpp =&p;
  p = &a;
  p = &b;
  lqq =&q;
  q = p;

  return *lq;
}

