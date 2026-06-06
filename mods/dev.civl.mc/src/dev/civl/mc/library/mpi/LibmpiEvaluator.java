package dev.civl.mc.library.mpi;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryEvaluator;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLTypeFactory;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.object.IntObject;

/**
 * <p>
 * <b>Summary</b> This class is an evaluator for evaluating expressions with MPI
 * specific semantics, including (partial) collective evaluation, semantics of
 * {@link MPIContractExpression}s and snap-shooting etc.
 * </p>
 * 
 * <p>
 * (Partial) Collective evaluation is an approach of evaluating expressions that
 * involving variables come from different MPI processes. Although it is one of
 * the most well-known feature of MPI that there is no shared storage between
 * any pair of MPI processes, one can use some auxiliary variables to expression
 * properties that involving a set of MPI processes and prove if they holds.
 * 
 * <ul>
 * <li><b>Collective evaluation c[E, comm, merge, Sp]:</b> A collective
 * evaluation is a tuple: a set of expressions E, an MPI communicator comm ,a
 * function merge(Sp) which maps a set of snapshots Sp to a state s and a set of
 * snapshots Sp. The MPI communicator comm associates to a set of MPI processes
 * P, for each process p in P, it matches a unique snapshot sp in Sp. Thus |Sp|
 * == |P|. The result of the collective evaluation is a set of symbolic values.
 * </li>
 * 
 * <li><b>Partial collective evaluation pc[E, comm, merge', Sp', s]:</b> A
 * partial collective evaluation is a tuple, in addition to the 4 elements of
 * c[E, comm, merge', Sp'], there is one more which is the current state s.
 * Compare to collective evaluation, there are some constraints: the function
 * merge'(Sp', s) maps a set of snapshots Sp' and a state s to a merged state
 * s'. Snapshots in Sp' are committed by the set of processes P', P' is a subset
 * of P. There exists one process set P'' which is also a subset of P. P' and
 * P'' are disjoint, the union of P' and P'' equals to P. s' consists of all
 * snapshots in Sp' and another set of snapshots Sp'' taken on s for processes
 * in P''. The result of the collective evaluation is a set of symbolic values.
 * .</li>
 * 
 * <li><b>Synchronization requirements [WP, a, comm, l]:</b>A synchronization
 * requirement is a tuple: A set of MPI processes WP, an assumption a , an MPI
 * communicator comm and a program location l. It expresses such a
 * synchronization property: It current process satisfies assumption a, the
 * current process can not keep executing until all processes in WP have reached
 * the location l. WP must be a subset of P which is associated to comm.</li>
 * </ul>
 * </p>
 * 
 * 
 * @author ziqingluo
 *
 */
public class LibmpiEvaluator extends BaseLibraryEvaluator implements LibraryEvaluator {
	public static int p2pCommField = 0;
	public static int colCommField = 1;
	public final IntObject queueIDField = universe.intObject(4);
	public final NumericExpression p2pCommIndexValue = zero;
	public final NumericExpression colCommIndexValue = one;
	public final static String mpiExtentName = "_uf_$mpi_extent";

	/* The MPI Datatypes, in the same order they occur in mpi-defs.cvh */
	public static enum Datatype {
		MPI_CHAR, MPI_CHARACTER, MPI_SIGNED_CHAR, MPI_UNSIGNED_CHAR, MPI_BYTE, MPI_WCHAR, MPI_SHORT, MPI_UNSIGNED_SHORT,
		MPI_INT, MPI_INT16_T, MPI_INT32_T, MPI_INT64_T, MPI_INT8_T, MPI_INTEGER, MPI_INTEGER1, MPI_INTEGER16,
		MPI_INTEGER2, MPI_INTEGER4, MPI_INTEGER8, MPI_UNSIGNED, MPI_LONG, MPI_UNSIGNED_LONG, MPI_FLOAT, MPI_DOUBLE,
		MPI_LONG_DOUBLE, MPI_LONG_LONG_INT, MPI_UNSIGNED_LONG_LONG, MPI_LONG_LONG, MPI_PACKED, MPI_LB, MPI_UB,
		MPI_UINT16_T, MPI_UINT32_T, MPI_UINT64_T, MPI_UINT8_T, MPI_FLOAT_INT, MPI_DOUBLE_INT, MPI_LONG_INT,
		MPI_SHORT_INT, MPI_2INT, MPI_LONG_DOUBLE_INT, MPI_AINT, MPI_OFFSET, MPI_2DOUBLE_PRECISION, MPI_2INTEGER,
		MPI_2REAL, MPI_C_BOOL, MPI_C_COMPLEX, MPI_C_DOUBLE_COMPLEX, MPI_C_FLOAT_COMPLEX, MPI_C_LONG_DOUBLE_COMPLEX,
		MPI_COMPLEX, MPI_COMPLEX16, MPI_COMPLEX32, MPI_COMPLEX4, MPI_COMPLEX8, MPI_REAL, MPI_REAL16, MPI_REAL2,
		MPI_REAL4, MPI_REAL8
	};

	public LibmpiEvaluator(String name, Evaluator evaluator, ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, evaluator, modelFactory, symbolicUtil, symbolicAnalyzer, civlConfig, libEvaluatorLoader);
	}

	public static Pair<CIVLPrimitiveType, NumericExpression> mpiTypeToCIVLType(SymbolicUniverse universe,
			CIVLTypeFactory typeFactory, int mpiType, CIVLSource source) {
		CIVLPrimitiveType primitiveType;
		NumericExpression count = universe.oneInt();
		Datatype datatype;

		if (mpiType >= 0 && mpiType < Datatype.values().length) {
			datatype = Datatype.values()[mpiType];
		} else {
			throw new CIVLUnimplementedFeatureException("CIVL doesn't have such a CIVLPrimitiveType", source);
		}

		switch (datatype) {
		case MPI_CHAR:
		case MPI_CHARACTER:
		case MPI_SIGNED_CHAR:
		case MPI_UNSIGNED_CHAR:
		case MPI_BYTE:
			primitiveType = typeFactory.charType();
			break;
		case MPI_SHORT:
		case MPI_UNSIGNED_SHORT:
		case MPI_INT:
		case MPI_INT16_T:
		case MPI_INT32_T:
		case MPI_INT64_T:
		case MPI_INT8_T:
		case MPI_INTEGER:
		case MPI_INTEGER1:
		case MPI_INTEGER16:
		case MPI_INTEGER2:
		case MPI_INTEGER4:
		case MPI_INTEGER8:
		case MPI_UNSIGNED:
		case MPI_LONG:
		case MPI_UNSIGNED_LONG:
		case MPI_LONG_LONG_INT:
		case MPI_UNSIGNED_LONG_LONG:
		case MPI_LONG_LONG:
		case MPI_LB:
		case MPI_UB:
		case MPI_UINT16_T:
		case MPI_UINT32_T:
		case MPI_UINT64_T:
		case MPI_UINT8_T:
		case MPI_AINT:
		case MPI_OFFSET:
			primitiveType = typeFactory.integerType();
			break;
		case MPI_FLOAT:
		case MPI_DOUBLE:
		case MPI_LONG_DOUBLE:
		case MPI_REAL:
		case MPI_REAL16:
		case MPI_REAL2:
		case MPI_REAL4:
		case MPI_REAL8:
			primitiveType = typeFactory.realType();
			break;
		case MPI_2INT:
		case MPI_2INTEGER:
		case MPI_SHORT_INT:
		case MPI_LONG_INT:
			primitiveType = typeFactory.integerType();
			count = universe.integer(2);
			break;
		default:
			throw new CIVLUnimplementedFeatureException("CIVL does not support datatype " + datatype, source);
		}
		return new Pair<>(primitiveType, count);
	}

}
