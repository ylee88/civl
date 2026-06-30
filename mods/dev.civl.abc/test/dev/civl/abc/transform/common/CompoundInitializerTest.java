package dev.civl.abc.transform.common;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.common.ASTPrettyPrinter;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;

public class CompoundInitializerTest {

	private static List<String> codes = Arrays.asList("prune", "sef");

	private String run(String source) throws ABCException {
		File file;

		try {
			file = File.createTempFile("compoundInit_", ".c");
			file.deleteOnExit();
			Files.writeString(file.toPath(), source);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			TranslationTask task = new TranslationTask(file);

			task.addAllTransformCodes(codes);

			AST ast = ABCExecutor.execute(task).getProgram().getAST();

			return normalize(ASTPrettyPrinter
					.prettyRepresentation(ast.getRootNode(), -1).toString());
		} finally {
			file.delete();
		}
	}

	private static String normalize(String text) {
		return text.replaceAll("//[^\n]*", " ").replaceAll("\\s+", " ").trim();
	}

	/**
	 * Runs ABC on the source and asserts its normalized pretty-printed output
	 * contains the expected snippet.
	 */
	private void check(String source, String expected) throws ABCException {
		String actual = run(source);
		String expectedNorm = normalize(expected);

		if (!actual.contains(expectedNorm))
			System.err.println("=== actual ===\n" + actual
					+ "\n====================================");
		assertTrue(actual.contains(expectedNorm));
	}

	@Test
	public void arrayInit() throws ABCException {
		check(/* Source code: */ """
				int f(int a) {
				  return a;
				}

				int main() {
				  int a[3] = {f(7), 8, 9};
				  return a[2];
				}
				""", /* Expected: */
				// TODO: Optimization: in some cases the zero initialization is not needed
				"""
						int main() {
						  int a[3];
						  for (int $sef$0 = 0; $sef$0 < 3; $sef$0 = $sef$0 + 1)
						    a[$sef$0] = 0;
						  a[0] = f(7);
						  a[1] = 8;
						  a[2] = 9;
						  return a[2];
						}
						""");
	}

	@Test
	public void structInit() throws ABCException {
		check(/* Source code: */ """
				struct Point {
				  int x;
				  int y;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Point p = {f(3), 4};
				  return p.x;
				}
				""", /* Expected: */ """
				int main() {
				  struct Point p;
				  p.x = 0;
				  p.y = 0;
				  p.x = f(3);
				  p.y = 4;
				  return p.x;
				}
				""");
	}

	@Test
	public void nestedInit() throws ABCException {
		check(/* Source code: */ """
				struct Inner {
				  int a;
				  int b;
				};

				struct Outer {
				  struct Inner in;
				  int arr[2];
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Outer o = {{f(1), 2}, {3, 4}};
				  return o.in.a;
				}
				""", /* Expected: */ """
				int main() {
				  struct Outer o;
				  o.in.a = 0;
				  o.in.b = 0;
				  for (int $sef$0 = 0; $sef$0 < 2; $sef$0 = $sef$0 + 1)
				    o.arr[$sef$0] = 0;
				    o.in.a = f(1);
				    o.in.b = 2;
				    o.arr[0] = 3;
				    o.arr[1] = 4;
				  return o.in.a;
				}
				""");
	}

	@Test
	public void complextDataStructure() throws ABCException {
		check(/* Source code: */ """
				struct Person {
				  char *name;
				  int age;
				};

				struct Team {
				  struct Person members[2];
				  char *label;
				  int score;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Team t = {
				  {{"Alice", f(30)}, {"Bob", 25}},
				  "dev",
				  f(100)
				  };
				  return t.score;
				}
				""", /* Expected: */ """
				int main() {
				  struct Team t;
				  struct Person $sef$0;
				  $sef$0.name = (char*)0;
				  $sef$0.age = 0;
				  for (int $sef$1 = 0; $sef$1 < 2; $sef$1 = $sef$1 + 1)
				    t.members[$sef$1] = $sef$0;
				  t.label = (char*)0;
				  t.score = 0;
				  char $sef$2[6];
				  $sef$2[0] = A;
				  $sef$2[1] = l;
				  $sef$2[2] = i;
				  $sef$2[3] = c;
				  $sef$2[4] = e;
				  $sef$2[5] = \\0;
				  t.members[0].name = $sef$2;
				  t.members[0].age = f(30);
				  char $sef$3[4];
				  $sef$3[0] = B;
				  $sef$3[1] = o;
				  $sef$3[2] = b;
				  $sef$3[3] = \\0;
				  t.members[1].name = $sef$3;
				  t.members[1].age = 25;
				  char $sef$4[4];
				  $sef$4[0] = d;
				  $sef$4[1] = e;
				  $sef$4[2] = v;
				  $sef$4[3] = \\0;
				  t.label = $sef$4;
				  t.score = f(100);
				  return t.score;
				}
				""");
	}

	@Test
	public void partialStruct() throws ABCException {
		check(/* Source code: */ """
				struct T {
				  int x;
				  int y;
				  int z;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct T t = {.x = f(1), .z = 3};
				  return t.z;
				}
				""", /* Expected: */ """
				int main() {
				  struct T t;
				  t.x = 0;
				  t.y = 0;
				  t.z = 0;
				  t.x = f(1);
				  t.y = 0;
				  t.z = 3;
				  return t.z;
				}
				"""); // TODO: optimization: the second 't.y = 0;' can be skipped
	}

	@Test
	public void partialStruct2() throws ABCException {
		check(/* Source code: */ """
				struct Config {
				  int a;
				  int b;
				  int c;
				  int d;
				};

				int f(int x) {
				  return x;
				}

				int main() {
				  struct Config cfg = {.a = f(1), .c = f(3)};
				  return cfg.c;
				}
				""", /* Expected: */ """
				int main() {
				  struct Config cfg;
				  cfg.a = 0;
				  cfg.b = 0;
				  cfg.c = 0;
				  cfg.d = 0;
				  cfg.a = f(1);
				  cfg.b = 0;
				  cfg.c = f(3);
				  cfg.d = 0;
				  return cfg.c;
				}
				""");
	}

	@Test
	public void partialStruct3() throws ABCException {
		check(/* Source code: */ """
				struct Person {
				  char *name;
				  int age;
				};

				struct Team {
				  struct Person lead;
				  struct Person deputy;
				  int score;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Team t = {.lead = {"Alice", f(30)},
				                   .deputy = {.age = f(25)},
								   .score = f(100)};
				  return t.score;
				}
				""", /* Expected: */ """
				int main() {
				  struct Team t;
				  t.lead.name = (char*)0;
				  t.lead.age = 0;
				  t.deputy.name = (char*)0;
				  t.deputy.age = 0;
				  t.score = 0;
				  char $sef$0[6];
				  $sef$0[0] = A;
				  $sef$0[1] = l;
				  $sef$0[2] = i;
				  $sef$0[3] = c;
				  $sef$0[4] = e;
				  $sef$0[5] = \\0;
				  t.lead.name = $sef$0;
				  t.lead.age = f(30);
				  t.deputy.name = 0;
				  t.deputy.age = f(25);
				  t.score = f(100);
				  return t.score;
				}
				""");
	}

	@Test
	public void stringInit() throws ABCException {
		check(/* Source code: */ """
				struct Str {
				  char *s;
				  int n;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Str x = {"hi", f(5)};
				  return x.n;
				}
				""", /* Expected: */ """
				int main() {
				  struct Str x;
				  x.s = (char*)0;
				  x.n = 0;
				  char $sef$0[3];
				  $sef$0[0] = h;
				  $sef$0[1] = i;
				  $sef$0[2] = \\0;
				  x.s = $sef$0;
				  x.n = f(5);
				  return x.n;
				}
				""");
	}

	@Test
	public void charArrayDirectInit() throws ABCException {
		check(/* Source code: */ """
				struct Buf {
				  char s[3];
				  int n;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Buf b = {"hi", f(1)};
				  return b.n;
				}
				""", /* Expected: */ """
				int main() {
				  struct Buf b;
				  for (int $sef$0 = 0; $sef$0 < 3; $sef$0 = $sef$0 + 1)
				    b.s[$sef$0] = 0;
				  b.n = 0;
				  b.s[0] = h;
				  b.s[1] = i;
				  b.s[2] = \\0;
				  b.n = f(1);
				  return b.n;
				}
				""");
	}

	@Test
	public void unionInit() throws ABCException {
		check(/* Source code: */ """
				union U {
				  int i;
				  double d;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  union U u = {f(42)};
				  return u.i;
				}
				""", /* Expected: */ """
				int main() {
				  union U u;
				  u.i = 0;
				  u.i = f(42);
				  return u.i;
				}
				""");
	}

	@Test
	public void arrayOfStructsInit() throws ABCException {
		check(/* Source code: */ """
				struct Point {
				  int x;
				  int y;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Point pts[2] = {{f(1), 2}, {3, 4}};
				  return pts[1].y;
				}
				""", /* Expected: */ """
				int main() {
				  struct Point pts[2];
				  struct Point $sef$0;
				  $sef$0.x = 0;
				  $sef$0.y = 0;
				  for (int $sef$1 = 0; $sef$1 < 2; $sef$1 = $sef$1 + 1)
				    pts[$sef$1] = $sef$0;
				  pts[0].x = f(1);
				  pts[0].y = 2;
				  pts[1].x = 3;
				  pts[1].y = 4;
				  return pts[1].y;
				}
				""");
	}

	@Test
	public void deepNestedInitFlatInit() throws ABCException {
		check(/* Source code: */ """
				struct Vec3 {
				  int x;
				  int y;
				  int z;
				};

				struct Transform {
				  struct Vec3 pos;
				  struct Vec3 scale;
				  int id;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Transform t = {f(1), 2, 3, 4, 5, 6, f(7)};
				  return t.id;
				}
				""", /* Expected: */ """
				int main() {
				  struct Transform t;
				  t.pos.x = 0;
				  t.pos.y = 0;
				  t.pos.z = 0;
				  t.scale.x = 0;
				  t.scale.y = 0;
				  t.scale.z = 0;
				  t.id = 0;
				  t.pos.x = f(1);
				  t.pos.y = 2;
				  t.pos.z = 3;
				  t.scale.x = 4;
				  t.scale.y = 5;
				  t.scale.z = 6;
				  t.id = f(7);
				  return t.id;
				}
				""");
	}

	@Test
	public void deepNestedInitFlatInit2() throws ABCException {
		check(/* Source code: */ """
				struct Vec3 {
				  int x;
				  int y;
				  int z;
				};

				struct Transform {
				  struct Vec3 pos;
				  struct Vec3 scale;
				  int id;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Transform t = {1, 2, 3, 4, 5, .pos = {6}, f(7)};
				  return t.scale.x;
				}
				""", /* Expected: */ """
				int main() {
				  struct Transform t;
				  t.pos.x = 0;
				  t.pos.y = 0;
				  t.pos.z = 0;
				  t.scale.x = 0;
				  t.scale.y = 0;
				  t.scale.z = 0;
				  t.id = 0;
				  t.pos.x = 6;
				  t.pos.y = 0;
				  t.pos.z = 0;
				  t.scale.x = f(7);
				  t.scale.y = 5;
				  t.scale.z = 0;
				  t.id = 0;
				  return t.scale.x;
				}
				""");
	}

	@Test
	public void anonymousField2() throws ABCException {
		check(/* Source code: */ """
				struct Person {
				  char *name;
				  int age;
				};

				struct Team {
				  struct {
				    struct Person members[2];
				    char *label;
				  };
				  int score;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  struct Team t = {
				    {{{"Alice", f(30)}, {"Bob", 25}},
				    "dev"},
				    f(100)
				  };
				  return t.score;
				}
				""", /* Expected: */ """
				int main() {
				  struct Team t;
				  t.score = 0;
				  char $sef$0[6];
				  $sef$0[0] = A;
				  $sef$0[1] = l;
				  $sef$0[2] = i;
				  $sef$0[3] = c;
				  $sef$0[4] = e;
				  $sef$0[5] = \\0;
				  t.members[0].name = $sef$0;
				  t.members[0].age = f(30);
				  char $sef$1[4];
				  $sef$1[0] = B;
				  $sef$1[1] = o;
				  $sef$1[2] = b;
				  $sef$1[3] = \\0;
				  t.members[1].name = $sef$1;
				  t.members[1].age = 25;
				  char $sef$2[4];
				  $sef$2[0] = d;
				  $sef$2[1] = e;
				  $sef$2[2] = v;
				  $sef$2[3] = \\0;
				  t.label = $sef$2;
				  t.score = f(100);
				  return t.score;
				}
				""");
	}

	@Test
	public void qualifiedStructInit() throws ABCException {
		check(/* Source code: */ """
				struct S {
				  volatile int x;
				  volatile int y;
				};

				int f(int a) {
				  return a;
				}

				int main() {
				  const struct S s = {f(1), 2};
				  return s.x;
				}
				""", /* Expected: */ """
				int main() {
				  struct S s;
				  s.x = 0;
				  s.y = 0;
				  s.x = f(1);
				  s.y = 2;
				  return s.x;
				}
				""");
	}

}
