%%

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

WHITE_SPACE_CHAR = [\n\r\ \t\b\012]

IDENT = [a-zA-Z][a-zA-Z0-9]*
STRING = '\''([^\']|\\')*'\'

SELECT = "SELECT"
FROM = "FROM"
WHERE = "WHERE"
AND = "&&"
ASTERISCO = "*"
EQUALS = "="
PONTO_VIRGULA = ";"

%%

"$TRACE_ON"    { yyparser.setDebug(true); }
"$TRACE_OFF"   { yyparser.setDebug(false); }

SELECT         { return AsdrSample.SELECT; }
FROM           { return AsdrSample.FROM; }
WHERE          { return AsdrSample.WHERE; }
&&              { return AsdrSample.AND; }
"*"             { return AsdrSample.ASTERISCO; }
"="             { return AsdrSample.EQUALS; }
";"             { return AsdrSample.PONTO_VIRGULA; }

{IDENT}         { return AsdrSample.IDENT; }

{STRING}        { return AsdrSample.STRING; }

{WHITE_SPACE_CHAR}+ {}

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
