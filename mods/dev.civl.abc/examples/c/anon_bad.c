// Example on anonymous members from C11 ...

struct v {
    union { // anonymous union
        struct {int i, j;}; // anonymous struct
        struct {long k, l;} w;
    };
    int m;
} v1;

int main() {
    v1.i = 2; // valid
    v1.w.k = 5; // valid
    v1.k = 3; // invalid
}
