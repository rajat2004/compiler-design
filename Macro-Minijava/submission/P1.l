%{ 
    #include <stdio.h>
    #include "P1.tab.h"
    #include <string.h>
%} 

digit [0-9]
integer (0|([1-9]{digit}*))
space "\t"|"\r"|"\f"|" "|"\n"
identifier [a-zA-Z_][a-zA-Z0-9_]*

%% 
"//".* { ; } 
{integer} { yylval.intval = atoi(yytext); return INTVAL; } 
"+" { yylval.op = strdup(yytext); return PLUS; } 
"-" { yylval.op = strdup(yytext); return MINUS; } 
"*" { yylval.op = strdup(yytext); return ASTR; }
"/" { yylval.op = strdup(yytext); return DIV; }
"!=" { yylval.op = strdup(yytext); return NOTEQUAL; }
"=" { yylval.op = strdup(yytext); return EQUAL; }
"!" { yylval.op = strdup(yytext); return NOT; }
"{" { yylval.op = strdup(yytext); return CBO; }
"}" { yylval.op = strdup(yytext); return CBC; }
"(" { yylval.op = strdup(yytext); return PO; }
")" { yylval.op = strdup(yytext); return PC; }
"[" { yylval.op = strdup(yytext); return SO; }
"]" { yylval.op = strdup(yytext); return SC; }
";" { yylval.op = strdup(yytext); return SCOLON; }
"&&" { yylval.op = strdup(yytext); return AND; }
"||" { yylval.op = strdup(yytext); return OR; }
"<=" { yylval.op = strdup(yytext); return LTORE; }
"." { yylval.op = strdup(yytext); return DOT; }
"," { yylval.op = strdup(yytext); return COMMA; }
"#" { yylval.op = strdup(yytext); return HASH; }
<<EOF>> { return END; }
{space}  { ; } 
"int"       { return INT; }
"this"      { return THIS; }
"new"       { return NEW; }
"public"    { return PUBLIC; }
"static"    { return STATIC; }
"void"      { return VOID; }
"main"      { return MAIN; }
"System.out.println" { return PRINT; }
"String"    { return STRING; }
"class"     { return CLASS; }
"if"        { return IF; }
"else"      { return ELSE; }
"while"     { return WHILE; }
"extends"   { return EXTENDS; }
"length"    { return LENGTH; }
"boolean"   { return BOOLEAN; }
"return"    { return RETURN; }
"define"   { return DEFINE; }
"false"|"true" { yylval.str = strdup(yytext);   return BOOL; }
{identifier} { yylval.str = strdup(yytext); return IDENTIFIER; }
%%

/*
{identifier} { yylval.integer = yytext[0] - 'a'; return Identifier; }
*/

