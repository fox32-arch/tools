#pragma once

#define STR2(x) #x
#define STR(x) STR2(x)

#define _call2(c, jt_addr)           \
    asm("li a0,ret_" #c "\n"         \
        "addi sp,sp,-4\n"            \
        "sw a0,0(sp)\n"              \
        "li a0,[" STR(jt_addr) "]\n" \
        "jr a0\n"                    \
        "ret_" #c ":"                \
        ::: "a0"                     \
    );
#define _call(c, jt_addr) _call2(c, jt_addr)
#define call(jt_addr) _call(__COUNTER__, jt_addr)

#define parameter(i, p) asm("mv x" #i ",%0" :: "r" (p) : "x" #i)

#define ret(i, var) asm("mv %0,x" #i : "=r" (var) :: "x" #i)
