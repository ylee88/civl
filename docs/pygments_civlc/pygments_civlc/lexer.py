from pygments.lexer import inherit
from pygments.lexers.c_cpp import CLexer
from pygments.token import Keyword, Name


class CivlCLexer(CLexer):
    name = "CIVL-C"
    aliases = ["civlc", "civl-c", "civl"]
    filenames = ["*.cvl", "*.civlc"]

    civl_functions = (
        "seq", "mem", "bundle", "read_set", "write_set",
    )

    civl_builtin_functions = (
        "assert", "assume", "assume_push", "assume_pop", "wait", "waitall",
        "exit", "choose_int", "elaborate", "elaborate_domain",
        "pathCondition", "is_concrete_int", "is_derefable", "get_state",
        "get_full_state", "malloc", "free", "havoc", "hide", "reveal",
        "hidden", "default_value", "pow", "remainder", "quotient",
        "next_time_count", "scope_parent", "local_start", "local_end",
        "yield", "print", "print_helper", "output_assign", "heap_size",
        "array_base_address_of", "unidirectional_when", "access", "read",
        "write", "calls", "scopeof", "is_terminated",
    )

    civl_constants = (
        "true", "false", "self", "here", "root", "result", "proc_null",
        "state_null", "nothing", "everything",
    )

    civl_types = (
        "proc", "scope", "state", "dynamic", "domain", "range", "real",
        "bundle", "mem", "memory", "message", "queue", "operation", "comm",
        "gcomm", "barrier", "gbarrier", "ibarrier", "gibarrier", "collator",
        "gcollator", "collect_checker", "gcollect_checker", "collate_state",
        "gcollate_state", "file", "filesystem", "omp_gteam", "omp_team",
        "omp_gshared", "omp_shared", "omp_helper_signal", "omp_var_status",
        "omp_work_record", "pthread_gpool_t", "pthread_pool_t",
        "cuda_stream_t", "cuda_stream_node_t", "cuda_thread_data_t",
        "cuda_warp_data_t", "loop_write_set",
    )

    civl_keywords = (
        "abstract", "system", "input", "output", "pure", "state_f",
        "atomic_f", "fatomic", "device", "global", "atomic", "when",
        "choose", "spawn", "forall", "exists", "uniform", "lambda",
        "parfor", "for", "requires", "ensures", "invariant", "depends",
        "assigns", "reads", "collective", "guards", "default",
    )

    ID_END = r"(?![A-Za-z0-9_])"

    tokens = {
        "statements": [
            (
                r"\$(?:seq|mem|bundle|read_set|write_set)_[A-Za-z0-9_]+" + ID_END,
                Name.Function,
            ),
            (
                r"\$(" + "|".join(civl_builtin_functions) + r")" + ID_END,
                Name.Function,
            ),
            (
                r"\$(" + "|".join(civl_constants) + r")" + ID_END,
                Name.Constant,
            ),
            (
                r"\$(" + "|".join(civl_types) + r")" + ID_END,
                Keyword.Type,
            ),
            (
                r"\$[A-Za-z_][A-Za-z0-9_]*_t" + ID_END,
                Keyword.Type,
            ),
            (
                r"\$(" + "|".join(civl_keywords) + r")" + ID_END,
                Keyword,
            ),
            (
                r"\$[A-Za-z_][A-Za-z0-9_]*",
                Name.Variable,
            ),
            inherit,
        ],
    }
