//===================== typedefHeader.h ====================
struct v_t;
typedef struct v_t V;
//====================== typedefMain.c =====================
int f() {
  return 0;
}
//===================== typedefHeader.h ====================
struct v_t;
typedef struct v_t V;
//===================== typedefSource.c ====================
struct v_t{
  int val;
};
int getVal(V* v) {
  return (v)->val;
}
int main() {
  V v0;
  return v0.val;
}