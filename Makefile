# This Makefile can be used to create a self-contained executable
# version of CIVL with its own custom Java Virtual Machine.  To build
# the application, you need a Java Development Kit.  To use the
# application, nothing is needed, not even a Java Virtual Machine.

# Instructions: Type "make".  This should produce a directory named
# civl-runtime, which contains everything needed to run CIVL.  Inside
# is a directory "bin" which contains the executable script "civl".
# You can move civl-runtime anywhere and then edit your PATH
# environment variable to contain the bin directory.  For example, add
# a line like
#
# export PATH=/path/to/civl-runtime/bin:$PATH
#
# in your .zprofile, .bash_profile, or similar startup file.  You may
# also consider editing civl by adding JVM options, e.g., to increase
# maximum heap size.

civl-runtime: lib/antlr3runtime.jar lib/dev.civl.gmc.jar lib/dev.civl.sarl.jar \
  lib/antlr4runtime.jar lib/dev.civl.abc.jar lib/dev.civl.mc.jar
	jlink --strip-debug --module-path "lib" \
  --add-modules "dev.civl.sarl,dev.civl.gmc,dev.civl.abc,dev.civl.mc,antlr3runtime,antlr4runtime,java.base" \
  --launcher "civl=dev.civl.mc/dev.civl.mc.CIVL" \
  --output "civl-runtime"

clean:
	rm -rf civl-runtime

.PHONY: clean
