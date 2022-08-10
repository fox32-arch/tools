" fox32 vim syntax highlighting

if exists("b:current_syntax")
  finish
endif

syn match foxIdentifier    /[_A-Za-z0-9]*/
syn region foxString start=/"/ end=/"/ skip=/\\"/
syn match foxCharacter     /'\(?\|\\?\)/
syn match foxDecimalNumber /\$\?-\?\d\+/
syn match foxBinaryNumber  /\$\?-\?0b[01]\+/
syn match foxHexNumber     /\$\?-\?0x\x\+/

syn match foxComment ";.*$"
syn match foxInclude "#.*$"

syn match foxRegisters /\vr(3[01]|[12]?\d|sp)/

syn match foxLabel /[_A-Za-z0-9]*:/

syn keyword foxInstructions nop halt brk reti ret ise icl
syn keyword foxInstructions inc dec not jmp call loop rjmp rcall rloop push pop int
syn keyword foxInstructions add sub mul div rem and or xor sla sra srl rol ror bse bcl bts cmp movz mov rta in out

syn keyword foxConditions ifz ifnz ifc ifnc ifgteq ifgt iglteq iflt

syn keyword foxData data

let b:current_syntax = "fox32"
hi def link foxIdentifier    Identifier
hi def link foxString        String
hi def link foxCharacter     Character
hi def link foxDecimalNumber Number
hi def link foxBinaryNumber  Number
hi def link foxHexNumber     Number
hi def link foxComment       Comment
hi def link foxInclude       Include
hi def link foxRegisters     Operator
hi def link foxLabel         Function
hi def link foxInstructions  Statement
hi def link foxConditions    Conditional
hi def link foxData          Keyword
