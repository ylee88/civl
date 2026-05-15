if exists("b:civl_loading")
  finish
endif

if expand('%:e') =~# '^\%(cvl\|cvh\)$'
  runtime! syntax/civl_overlay.vim
endif
