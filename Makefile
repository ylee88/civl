
# This Makefile can be used to create a self-contained executable
# version of CIVL with its own custom Java Virtual Machine.  To build
# the application, you need a Java Development Kit.  To use the
# application, nothing is needed, not even a Java Virtual Machine.

# Instructions: Type "make".  This should produce a directory named
# civl-runtime, which contains everything needed to run CIVL.  Inside
# is a directory "bin" which contains the executable "civl".  You can
# move civl-runtime anywhere and create a symlink to the executable
# for convenience.

civl-runtime: lib/antlr3runtime.jar lib/dev.civl.gmc.jar lib/dev.civl.sarl.jar \
  lib/antlr4runtime.jar lib/dev.civl.abc.jar lib/dev.civl.mc.jar
	jlink --module-path "lib" \
  --add-modules "dev.civl.sarl,dev.civl.gmc,dev.civl.abc,dev.civl.mc,antlr3runtime,antlr4runtime,java.base" \
  --launcher "civl=dev.civl.mc/dev.civl.mc.CIVL" \
  --output "civl-runtime"
