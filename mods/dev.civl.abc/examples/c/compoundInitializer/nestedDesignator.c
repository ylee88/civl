// TODO: CIVL cannot parse this example:
struct Pt {
    int x;
    int y;
};

struct Line {
    struct Pt s;
    struct Pt e;
};

int f() {
    struct Line L = {1, 2, .s.x = 3};
}
