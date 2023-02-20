*** Reaching Definitions ***
*** InSet Map ***
Node[-1, 0, ifthen.c:2.0-10.1 "int main() {\n   ... }"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[31, 19, ifthen.c:9.2-8 "x = 42"]>
Node[10, 20, ifthen.c:2.11-10.1 "{\n  x = 7; ... }"]
Node[11, 5, ifthen.c:3.2-7 "x = 7"]
Node[17, 7, ifthen.c:4.6-11 "x > 0"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[11, 5, ifthen.c:3.2-7 "x = 7"]>
Node[22, 10, ifthen.c:5.4-7 "x++"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[11, 5, ifthen.c:3.2-7 "x = 7"]>
Node[27, 14, ifthen.c:7.4-7 "x--"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[11, 5, ifthen.c:3.2-7 "x = 7"]>
Node[31, 19, ifthen.c:9.2-8 "x = 42"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[22, 10, ifthen.c:5.4-7 "x++"]>  #  <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[27, 14, ifthen.c:7.4-7 "x--"]>
*** OutSet Map ***
Node[-1, 0, ifthen.c:2.0-10.1 "int main() {\n   ... }"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[31, 19, ifthen.c:9.2-8 "x = 42"]>
Node[10, 20, ifthen.c:2.11-10.1 "{\n  x = 7; ... }"]
Node[11, 5, ifthen.c:3.2-7 "x = 7"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[11, 5, ifthen.c:3.2-7 "x = 7"]>
Node[17, 7, ifthen.c:4.6-11 "x > 0"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[11, 5, ifthen.c:3.2-7 "x = 7"]>
Node[22, 10, ifthen.c:5.4-7 "x++"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[22, 10, ifthen.c:5.4-7 "x++"]>
Node[27, 14, ifthen.c:7.4-7 "x--"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[27, 14, ifthen.c:7.4-7 "x--"]>
Node[31, 19, ifthen.c:9.2-8 "x = 42"] ==> <Entity[kind=VARIABLE, name=x, linkage=EXTERNAL, type=0, definition=1]@Node[31, 19, ifthen.c:9.2-8 "x = 42"]>
