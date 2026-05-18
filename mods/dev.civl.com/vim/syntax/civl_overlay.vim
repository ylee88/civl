if exists("b:current_syntax") && b:current_syntax ==# "civl"
  finish
endif

syntax match civlIdentifier /\$[A-Za-z_][A-Za-z0-9_]*/

syntax match civlFunction /\$\%(seq\|mem\|bundle\|read_set\|write_set\)_[A-Za-z0-9_]\+\>/
syntax match civlFunction /\$\%(assert\|assume\|assume_push\|assume_pop\|wait\|waitall\|exit\|choose_int\|elaborate\|elaborate_domain\|pathCondition\|is_concrete_int\|is_derefable\|get_state\|get_full_state\|malloc\|free\|havoc\|hide\|reveal\|hidden\|default_value\|pow\|remainder\|quotient\|next_time_count\|scope_parent\|local_start\|local_end\|yield\|print\|print_helper\|output_assign\|heap_size\|array_base_address_of\|unidirectional_when\|access\|read\|write\|calls\)\>/

syntax match civlConstant /\$\%(true\|false\|self\|here\|root\|result\|proc_null\|state_null\|nothing\|everything\)\>/

syntax match civlType /\$\%(proc\|scope\|state\|dynamic\|domain\|range\|real\|bundle\|mem\|memory\|message\|queue\|operation\|comm\|gcomm\|barrier\|gbarrier\|ibarrier\|gibarrier\|collator\|gcollator\|collect_checker\|gcollect_checker\|collate_state\|gcollate_state\|file\|filesystem\|omp_gteam\|omp_team\|omp_gshared\|omp_shared\|omp_helper_signal\|omp_var_status\|omp_work_record\|pthread_gpool_t\|pthread_pool_t\|cuda_stream_t\|cuda_stream_node_t\|cuda_thread_data_t\|cuda_warp_data_t\|loop_write_set\)\>/
syntax match civlType /\$[A-Za-z_][A-Za-z0-9_]*_t\>/

syntax match civlKeyword /\$\%(abstract\|system\|input\|output\|pure\|state_f\|atomic_f\|fatomic\|device\|global\|atomic\|when\|choose\|spawn\|forall\|exists\|uniform\|lambda\|parfor\|for\|requires\|ensures\|invariant\|depends\|assigns\|reads\|collective\|guards\)\>/

highlight default link civlKeyword Keyword
highlight default link civlType Type
highlight default link civlConstant Constant
highlight default link civlFunction Function
highlight default link civlIdentifier Identifier

let b:current_syntax = "civl"
