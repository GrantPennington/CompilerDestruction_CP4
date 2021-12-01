# CompilerDestruction_CP4
Author: Grant Pennington
This is my final project for my Compiler Construction, CS322 Course.

I built a simple compiler for a toy language called KnightCode 
using ANTLR libraries to generate the lexer/parser and ASM libraries for bytecode generation.
The ASM code is written using java and KnightCode compiles to JVM Bytecode.

Functions KnightCode has:
- Variables (Only accepts Integer and String values)
  1. Declaration -> INTEGER x
  2. Assignment -> SET x := 0
 - Basic operations like addition, subtraction, multiplication, and division (+, -, *, /)
 - Print statements -> PRINT
 - While loops -> WHILE x > y DO
                      // DO STUFF HERE
                  ENDWHILE
- IF Statements -> IF x < y THEN ELSE 

!! Unfortunately, I did not have time to implement IF statements yet, so those are not included in my compiler. !!

INSTRUCTIONS FOR USING COMPILER:
- Use ant to build your lexer/parser from ANTLR
- In the command line -> $java compiler/kcc tests/program1.kc output/Program1
- This will create a .class file called Program1.class that can be executed in the command line 
-> $java output/Program1
