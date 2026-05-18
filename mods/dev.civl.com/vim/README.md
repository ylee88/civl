# CIVL-C Syntax for Vim

This directory contains a small Vim/Neovim syntax bundle for CIVL-C.

## Features

- Automatic filetype detection for `*.cvl` and `*.cvh`
- `filetype=c` is preserved so C-oriented Vim plugins keep working
- C syntax reused as the base highlighting
- CIVL-specific `$...` constructs highlighted on top of the standard C syntax
- Manual opt-in for CIVL-flavored `*.c` and `*.h` files

## Installation

### Vim

```sh
mkdir -p ~/.vim/pack/civl/start
ln -s /path/to/civl/mods/dev.civl.com/vim ~/.vim/pack/civl/start/civl
```

### Neovim

```sh
mkdir -p ~/.local/share/nvim/site/pack/civl/start
ln -s /path/to/civl/mods/dev.civl.com/vim ~/.local/share/nvim/site/pack/civl/start/civl
```

### Using [vim-plug](https://github.com/junegunn/vim-plug) (Recommended)

For a local CIVL clone, the simplest setup is to point `vim-plug` directly at this `vim/` directory:

```vim
call plug#begin()

Plug '/path/to/civl/mods/dev.civl.com/vim'

call plug#end()
```

Or, pulling from GitHub directly,

```vim
call plug#begin()

Plug 'verified-software-lab/civl', { 'rtp': 'mods/dev.civl.com/vim' }

call plug#end()
```

## Usage

- Open a `*.cvl` or `*.cvh` file and Vim will keep `filetype=c` while loading CIVL-specific syntax highlighting automatically.
- For CIVL-flavored `*.c` or `*.h` files, switch the syntax manually while keeping the filetype as C:

```vim
:set syntax=civl
```

Unknown CIVL-specific `$...` identifiers intentionally fall back to the normal `Identifier` highlight group.
