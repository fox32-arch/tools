# Compiling C code for fox32

How does this work?

- GCC compiles your C code with `riscv*-gcc -S hello.c -march=rv32im -mabi=ilp32 -O2`
- `rv2fox` converts the generated RISC-V assembly to fox32 assembly
- `fox32asm` makes an FXF binary

## Generating bindings.c

bindings.kts is a little Kotlin script which generates bindings.c.
Ensure you have Kotlin installed on your system, then run `./bindings.kts > bindings.c`

## TODO:

- commandline argument passing
- testcase sha256 program that hashes the ROM
- rust support: `rustc --emit asm --target riscv32im-unknown-none-elf test.rs -O`
