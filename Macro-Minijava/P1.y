%{
    #define _GNU_SOURCE
    #include <stdio.h>
    #include <string.h>
    #include <stdlib.h>
    #define MAX_SIZE 1000000
    #define MACRO_SIZE 1000
    int yyerror (char* h);
    int yylex(void);
    
    
char* EmptyStr(){
    char* ret;
    asprintf(&ret,"");
    return ret;
}



struct macro_{
    char* name;
    int num_args;
    char* out;
};

typedef struct macro_ macro;

macro* macroList[MACRO_SIZE];
int num_macro = 0;

int NumArguments(char* arr){
    int i, count;
    i = 0;
    count = 0;
    while( i < strlen(arr) ) {
        if(arr[i] == ',' )
            count++;
        i++;
    }

    return arr[0] != '\0' ? count+1 : count;
}

char* ModifyMacroExpression(char* arg, int num_args, char* exp) {
    // return exp;
    // For each argument, replace the arg in exp with the number of the argument
    int i = 0;
    // char** arguments;

    char** arguments = (char**)malloc((num_args+2)*sizeof(char*));
    for(i=0; i<num_args; i++)
        arguments[i]=(char*)malloc((MACRO_SIZE+5)*sizeof(char));
     
    int k=0,j=0;

    for (i=0; i<strlen(arg); i++) {
        if (arg[i] != ',') {
            arguments[j][k] = arg[i];
            k++;
        }
        else {
            arguments[j][k] = '\0';
            j++;
            k=0;
        }
    }
    arguments[j][k] = '\0';

    char* buffer = malloc(MAX_SIZE);

    // printf("%s\n", exp);
    for (i=0; i<num_args; i++) {
        char* p = exp;
        while((p=strstr(p, arguments[i]))){
            char* argNum;
            asprintf(&argNum, "#^%d", i);
            
            strncpy(buffer, exp, p-exp);
            buffer[p-exp] = '\0';
            strcat(buffer, argNum);
            strcat(buffer, p+strlen(arguments[i]));
            strcpy(exp, buffer);
            p++;
        }
    }
    // printf("%s\n", exp);
    return exp;
}

void MacroStore(char* name, char* arg, char* exp){
    macro* temp = (macro*)malloc( sizeof(macro) );
    asprintf(&temp->name,"%s", name);
    temp->num_args = NumArguments(arg);
    if (temp->num_args == 0)
        asprintf(&temp->out, "%s", exp);
    else 
        asprintf(&temp->out, "%s", ModifyMacroExpression(arg, temp->num_args, exp));
    macroList[num_macro] = temp;
    // printf("%s\n", name);
    num_macro++;
}

char* ReplaceMacro(char* exp, macro* temp ){
    if (exp && exp[0]=='\0')
        return temp->out;
    else {
        // return temp->out;
        int i = 0;
        int num_args = NumArguments(exp);

        char** arguments = (char**)malloc((num_args+2)*sizeof(char*));
        for(i=0; i<num_args; i++)
            arguments[i]=(char*)malloc((MACRO_SIZE+5)*sizeof(char));
     
        int k=0,j=0;

        for (i=0; i<strlen(exp); i++) {
            if (exp[i] != ',') {
                arguments[j][k] = exp[i];
                k++;
            }
            else {
                arguments[j][k] = '\0';
                j++;
                k=0;
            }
        }
        arguments[j][k] = '\0';

        char* exp2 = temp->out;

        for (i=0; i<num_args; i++) {
            char* argNum;
            asprintf(&argNum, "#^%d", i);

            char* buffer = malloc(MAX_SIZE);
            char* p = exp2;
            
            while((p=strstr(p, argNum))){
                strncpy(buffer, exp2, p-exp2);
                buffer[p-exp2] = '\0';
                strcat(buffer, arguments[i]);
                strcat(buffer, p+strlen(argNum));
                strcpy(exp2, buffer);
                p++;
            }

        }
        return exp2;
    }
}
%}

%union
{
    int intval;
    char* id;
    char* str;
    char* op;   // For +,-,*
}

%start Goal;
%type <str> Goal MacroDefinitionTemp MainClass Expression PrimaryExpression TypeDeclaration TypeDeclarationTemp MethodDeclaration MethodDeclarationTemp Type TempType Argument Statement StatementTemp ExpressionTemp IDENTIFIERParam MacroDefExpression MacroDefStatement MacroDefinition

%token<str> IDENTIFIER BOOL 
%token<intval> INTVAL
%token<op> PLUS MINUS ASTR DIV EQUAL AND NOTEQUAL DOT LTORE OR CBO CBC PO PC SO SC SCOLON NOT COMMA HASH
%token THIS NEW PUBLIC STATIC VOID MAIN INT PRINT STRING CLASS END IF ELSE WHILE EXTENDS LENGTH BOOLEAN RETURN DEFINE

%%

Goal : MacroDefinitionTemp MainClass TypeDeclarationTemp END 
       { printf("%s\n%s", $2, $3); }
;

MacroDefinitionTemp: MacroDefinitionTemp MacroDefinition	
                     { 
                        char* ret ; 
                        asprintf(&ret, "%s\n%s", $1, $2 ); 
                        free($1); free($2);	$$ = ret; 
                     }
                   | { $$ = EmptyStr(); }
;

MainClass : CLASS IDENTIFIER CBO PUBLIC STATIC VOID MAIN PO STRING SO SC IDENTIFIER PC CBO PRINT PO Expression PC SCOLON CBC CBC
{
    char* ret  ;
    asprintf(&ret, "class %s {\n\tpublic static void main (String [] %s) {\n\t\t System.out.println(%s); \n}\n}", $2, $12, $17);
    $$ = ret;
}
;


TypeDeclarationTemp: TypeDeclarationTemp TypeDeclaration
                     { 
                        char* ret  ; 
                        asprintf(&ret, "%s\n%s", $1, $2 ); 
                        free($1); free($2); $$ = ret; 
                     }
                   | { $$ = EmptyStr(); }
;

TypeDeclaration : CLASS IDENTIFIER CBO TempType MethodDeclarationTemp CBC
                  {
                    char* ret  ; 
                    asprintf(&ret,"class %s {\t%s\t%s\n}", $2, $4, $5 ); 
                    free($4); free($5); $$ = ret;
                  }
                  
                | CLASS IDENTIFIER EXTENDS IDENTIFIER CBO TempType MethodDeclarationTemp CBC
                  {
                    char* ret  ; 
                    asprintf(&ret,"class %s extends %s \n%s\n%s\n}", $2, $4, $5, $7 ); 
                    free($4); free($5); $$ = ret;
                  }
                  
;

MethodDeclarationTemp: MethodDeclarationTemp MethodDeclaration	
                       { 
                            char* ret  ; 
                            asprintf(&ret,"\t%s\n\t%s", $1, $2); 
                            free($1); free($2); $$ = ret; 
                        }
                     |	{ $$ = EmptyStr(); }
;


MethodDeclaration : PUBLIC Type IDENTIFIER PO Argument PC CBO TempType StatementTemp RETURN Expression SCOLON CBC
                    {
                        char* ret  ; 
                        asprintf(&ret, "public %s %s (%s) {\n\t%s \n%s \nreturn %s;\n}", $2, $3, $5, $8, $9, $11); 
                        free($2); free($5); free($8); free($9); free($11); $$ = ret;
                    }
;

Argument: Argument COMMA Type IDENTIFIER	
          { 
            char* ret; 
            asprintf(&ret,"%s , %s %s", $1, $3, $4); 
            free($1); free($3); $$ = ret; 
          }
          
        | Type IDENTIFIER	
          { 
            char* ret ; 
            asprintf(&ret,"%s %s", $1, $2); 
            free($1); $$ = ret; 
          }
          
        | { $$ = EmptyStr(); }
;

TempType : TempType Type IDENTIFIER SCOLON
           {
                char* ret  ;
                asprintf(&ret, "\t%s \n\t%s %s;", $1, $2, $3);
                free($1); free($2); $$ = ret;
           }
           
         | { $$ = EmptyStr(); }
;

Type : INT SO SC
       {
            char* ret  ;
            asprintf(&ret, "int []");
            $$ = ret;
       }
       
     | INT
       {
            char* ret  ;
            asprintf(&ret, "int");
            $$ = ret;
       }
       
     | BOOLEAN
       {
            char* ret  ;
            asprintf(&ret, "boolean");
            $$ = ret;
       }
       
     | IDENTIFIER	
       { 
            char* ret  ; 
            asprintf(&ret,"%s", $1 ); 
            $$ = ret; 
       }
;


StatementTemp : Statement StatementTemp
                {
                    char* ret  ;
                    asprintf(&ret, "\t%s\n\t%s", $1, $2);
                    free($1); free($2); $$ = ret;
                }
                
              | { $$ = EmptyStr(); }
;


Statement : CBO StatementTemp CBC	
            { 
                char* ret  ; 
                asprintf(&ret,"\t{\n%s\n}", $2); 
                free($2); $$ = ret; 
            }

          | PRINT PO Expression PC SCOLON
            {
                char* ret  ;
                asprintf(&ret, "System.out.println(%s);", $3);
                free($3); $$ = ret;
            }
            
          | IDENTIFIER EQUAL Expression SCOLON
            {
                char* ret  ;
                asprintf(&ret, "%s = %s;", $1, $3);
                free($3); $$ = ret;
            }
            
          | IDENTIFIER SO Expression SC EQUAL Expression SCOLON
            {
                char* ret  ;
                asprintf(&ret, "%s[%s] = %s;", $1, $3, $6);
                free($1); free($3); free($6);
                $$ = ret;
            }
            
          | IF PO Expression PC Statement	
            { 
                char* ret ; 
                asprintf(&ret,"\t if (%s) \n\t%s", $3, $5); 
                free($3); free($5); $$ = ret; 
            }
            
          | IF PO Expression PC Statement ELSE Statement	
            { 
                char* ret  ; 
                asprintf(&ret,"\tif (%s) \n\t%s \n\telse \n\t%s", $3, $5, $7); 
                free($3); free($5); free($7); $$ = ret; 
            }
           
          | WHILE PO Expression PC Statement	
            { 
                char* ret; 
                asprintf(&ret,"\twhile (%s)\n\t%s", $3, $5); 
                free($3); free($5); $$ = ret; 
            }

          | IDENTIFIER PO ExpressionTemp PC SCOLON	
            { 
                char* ret;
                int i;
                macro* temp;
                for (i=0; i<num_macro; i++) {
                    temp = macroList[i];
                    if( !strcmp(temp->name, $1 ) && temp->num_args == NumArguments($3) ){ 
                        asprintf(&ret,"\t%s", ReplaceMacro($3, temp) ); 
                        break; 
                    }
                } 
                free($3); $$ = ret; 
            }
;



Expression : PrimaryExpression AND PrimaryExpression 
             { 
                char* ret;
                asprintf(&ret, "%s && %s", $1, $3);
                $$ = ret; 
             }
        
           | PrimaryExpression OR PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s || %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression NOTEQUAL PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s != %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression LTORE PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s <= %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression PLUS PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s + %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression MINUS PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s - %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression ASTR PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s * %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression DIV PrimaryExpression
             { 
                char* ret;
                asprintf(&ret, "%s / %s", $1, $3);
                $$ = ret; 
             }

           | PrimaryExpression SO PrimaryExpression SC
             { 
                char* ret;
                asprintf(&ret, "%s [ %s ]", $1, $3);
                $$ = ret; 
             }
             
           | PrimaryExpression DOT LENGTH
             {
                char* ret;
                asprintf(&ret, "%s.length", $1);
                $$ = ret;
             }

           | PrimaryExpression DOT IDENTIFIER PO ExpressionTemp PC
             {
                char* ret; 
                asprintf(&ret,"%s.%s(%s)", $1, $3, $5); 
                free($1); free($3); free($5); $$ = ret;
             }

           | IDENTIFIER PO ExpressionTemp PC		
             { 
                char* ret; 
                int i;
                macro* temp;
                for (i=0; i<num_macro; i++) {
                    temp = macroList[i];
                    if( !strcmp(temp->name, $1 ) && temp->num_args == NumArguments($3) ){ 
                        asprintf(&ret,"(%s)", ReplaceMacro($3, temp) ); 
                        break; 
                    }
                } 
                free($3); $$ = ret; 
             }

           | PrimaryExpression
             { 
                char* ret  ;
                asprintf(&ret, "%s", $1);
                $$ = ret;
             }
;

ExpressionTemp: ExpressionTemp COMMA Expression
                {
                    char* ret  ; 
                    asprintf(&ret,"%s,%s", $1, $3); 
                    free($1); free($3); $$ = ret;
                }

              | Expression	
                { 
                    char* ret  ; 
                    asprintf(&ret,"%s", $1); 
                    free($1); $$ = ret; 
                }
              |	{ $$ = EmptyStr(); }
;


PrimaryExpression : INTVAL
                    { 
                        char* ret  ;
                        asprintf(&ret, "%d", $1);
                        $$ = ret;
                    }

                  | BOOL
                    {
                        char* ret  ;
                        asprintf(&ret, "%s", $1);
                        $$ = ret;
                    }
                    
                  | THIS
                    {
                        char* ret  ;
                        asprintf(&ret, "this");
                        $$ = ret;
                    }
                    
                  | NEW INT SO Expression SC
                    { 
                        char* ret  ;
                        asprintf(&ret, "new int [%s]", $4);
                        $$ = ret; 
                    }
                    
                  | NEW IDENTIFIER PO PC
                    { 
                        char* ret  ;
                        asprintf(&ret, "new %s()", $2);
                        $$ = ret; 
                    }
                    
                  | NOT Expression
                    { 
                        char* ret  ;
                        asprintf(&ret, "! %s", $2);
                        $$ = ret; 
                    }
                    
                  | PO Expression PC
                    { 
                        char* ret  ;
                        asprintf(&ret, "(%s)", $2);
                        $$ = ret; 
                    }
                    
                  | IDENTIFIER
                    { 
                        char* ret  ;
                        asprintf(&ret, "%s", $1);
                        $$ = ret; 
                    }
;


MacroDefinition: MacroDefStatement	
                 { $$ = EmptyStr(); }
               | MacroDefExpression	
                 { $$ = EmptyStr(); }
;

MacroDefStatement: HASH DEFINE IDENTIFIER PO IDENTIFIERParam PC CBO StatementTemp CBC 
                   { 
                       MacroStore($3, $5, $8); 
                       free($5); free($8); free($3);
                   }
;
  
MacroDefExpression: HASH DEFINE IDENTIFIER PO IDENTIFIERParam PC PO Expression PC 
                    { 
                        MacroStore($3, $5, $8); 
                        free($5); free($8); free($3);
                    }
;

IDENTIFIERParam: IDENTIFIERParam COMMA IDENTIFIER	
                 { 
                    char* ret ; 
                    asprintf(&ret,"%s,%s", $1, $3); 
                    free($1); $$ = ret; 
                 }

               | IDENTIFIER	
                 { 
                    char* ret ; 
                    asprintf(&ret,"%s", $1); 
                    $$ = ret; 
                 }

               | { $$ = EmptyStr(); }
;

%%

int yyerror(char *s)
{
    printf ("// Failed to parse macrojava code.\n");
    return 0;
}
int main ()
{
    yyparse();
    return 0;
}
