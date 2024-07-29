grammar LA;

/*
Gramática da linguagem LA para o Lexer
*/

// Palavras reservadas
ALGORITMO : 'algoritmo';
DECLARE : 'declare';
REAL: 'real';
LITERAL : 'literal';
INTEIRO : 'inteiro';
LEIA : 'leia';
ESCREVA : 'escreva';
FIM_ALGORITMO : 'fim_algoritmo';
TIPO: 'tipo';
VAR: 'var';
CONSTANTE: 'constante';
REGISTRO: 'registro';
FIM_REGISTRO: 'fim_registro';
FALSO: 'falso';
VERDADEIRO: 'verdadeiro';
OU: 'ou';
E: 'e';
NAO: 'nao';
LOGICO: 'logico';

// Loops e condicionais
SE: 'se';
FIM_SE: 'fim_se';
SENAO: 'senao';
ENQUANTO: 'enquanto';
FIM_ENQUANTO: 'fim_enquanto';
PARA: 'para';
FIM_PARA: 'fim_para';
ENTAO: 'entao';
FACA: 'faca';
ATE: 'ate';
PROCEDIMENTO: 'procedimento';
FIM_PROCEDIMENTO: 'fim_procedimento';
FUNCAO: 'funcao';
FIM_FUNCAO: 'fim_funcao';
RETORNE: 'retorne';
CASO: 'caso';
FIM_CASO: 'fim_caso';
SEJA: 'seja';

PONTO_PONTO: '..';
PONTO: '.';

// Trata cadeias
CADEIA : '"' ( ~["\r\n] | '""' )* '"';
WS 	:	( ' ' |'\t' | '\r' | '\n') {skip();}
	;

// Trata comentários
COMENTARIO : '{' ~('\n'|'\r'|'}')* '}' -> skip;

// Trata caracteres especiais
DOIS_PONTOS: ':';
ABREPAR: '(';
FECHAPAR: ')';
VIRGULA: ',';
ABRECOL: '[';
FECHACOL: ']';

// Operadores
DIFERENTE: '<>';
MAIOR_IGUAL: '>=';
MENOR_IGUAL: '<=';
ATRIBUICAO: '<-';
IGUAL: '=';
MAIS: '+';
MENOS: '-';
MULTIPLICACAO: '*';
DIVISAO: '/';
MAIOR: '>';
MENOR: '<';
MOD: '%';
PONTEIRO: '^';
ENDERECO: '&';

// Trata números
NUM_INT	: ('0'..'9')+;
NUM_REAL : ('0'..'9')+ ('.' ('0'..'9')+)?;

// Trata identificadores
IDENT : [a-zA-Z_][a-zA-Z_0-9]*;

COMENTARIO_NAO_FECHADO: '{' ~('\n'|'}')* '\n';
CADEIA_NAO_FECHADA: '"' ~('\n'|'"')* '\n';

// Simbolos não reconhecidos na linguagem
ERRO: '~' | '$' | '}' | '|' | '!' | '@' ;



/*
Gramática da linguagem LA para o Parser
*/

programa: declaracoes 'algoritmo' corpo 'fim_algoritmo' EOF;
declaracoes: (declaracao_local | declaracao_global)*;
declaracao_local: declaracao_variavel | declaracao_constante | declaracao_tipo; 
declaracao_variavel: 'declare' variavel;
declaracao_constante: 'constante' IDENT ':' tipo_basico '=' valor_constante;
declaracao_tipo: 'tipo' IDENT ':' tipo; 
variavel: identificador (',' identificador)* ':' tipo;
identificador: IDENT ('.' IDENT)* dimensao;
dimensao: ('[' exp_aritmetica ']')*;
tipo: registro | tipo_estendido;
tipo_basico: 'literal' | 'inteiro' | 'real' | 'logico';
tipo_basico_ident: tipo_basico | IDENT;
tipo_estendido: '^'? tipo_basico_ident;
valor_constante: CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';
registro: 'registro' variavel* 'fim_registro';
declaracao_global:
    'procedimento' IDENT '(' parametros? ')' corpo 'fim_procedimento' | 'funcao' IDENT '(' parametros? ')' ':' tipo_estendido corpo 'fim_funcao';
parametro: 'var'? identificador (',' identificador)* ':' tipo_estendido;
parametros: parametro (',' parametro)*;
corpo: declaracao_local* cmd*;
cmd: cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne;
cmdLeia: 'leia' '(' '^'? identificador (',' '^'? identificador)* ')';
cmdEscreva: 'escreva' '(' expressao (',' expressao)* ')';
cmdSe: 'se' expressao 'entao' cmd* ('senao' cmd*)? 'fim_se';
cmdCaso: 'caso' exp_aritmetica 'seja' selecao ('senao' cmd*)? 'fim_caso';
cmdPara: 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' cmd* 'fim_para';
cmdEnquanto: 'enquanto' expressao 'faca' cmd* 'fim_enquanto';
cmdFaca: 'faca' cmd* 'ate' expressao;
cmdAtribuicao: '^'? identificador '<-' expressao;
cmdChamada: IDENT '(' expressao (',' expressao)* ')';
cmdRetorne: 'retorne' expressao;
selecao: item_selecao*;
item_selecao: constantes ':' cmd*;
constantes: numero_intervalo (',' numero_intervalo)*;
numero_intervalo: op_unario? NUM_INT ( '..' op_unario? NUM_INT)?;
op_unario: '-';
exp_aritmetica: termo (op1 termo)*;
termo: fator (op2 fator)*;
fator: parcela (op3 parcela)*;
op1: '+' | '-';
op2: '*' | '/';
op3: '%';
parcela: op_unario? parcela_unario | parcela_nao_unario;
parcela_unario: '^'? identificador | IDENT '(' expressao (',' expressao)* ')' | NUM_INT | NUM_REAL | '(' expressao ')';
parcela_nao_unario: '&' identificador | CADEIA;
exp_relacional: exp_aritmetica (op_relacional exp_aritmetica)?;
op_relacional: '=' | '<>' | '>=' | '<=' | '>' | '<';
expressao: termo_logico (op_logico_1 termo_logico)*;
termo_logico: fator_logico (op_logico_2 fator_logico)*;
fator_logico: 'nao'? parcela_logica;
parcela_logica: 'verdadeiro' | 'falso' | exp_relacional;
op_logico_1: 'ou';
op_logico_2: 'e';
