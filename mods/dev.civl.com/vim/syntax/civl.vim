if exists("b:current_syntax") && b:current_syntax ==# "civl"
  finish
endif

let b:civl_loading = 1
runtime! syntax/c.vim
unlet b:civl_loading

runtime! syntax/civl_overlay.vim
