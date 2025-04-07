import java.io.*;

public class AsdrSample {

  private static final int BASE_TOKEN_NUM = 301;

  public static final int IDENT = 301;
  public static final int STRING = 302;
  public static final int SELECT = 303;
  public static final int FROM = 304;
  public static final int WHERE = 305;
  public static final int AND = 306;
  public static final int ASTERISCO = 307;
  public static final int EQUALS = 308;
  public static final int PONTO_VIRGULA = 309;

  public static final String[] tokenList = {
    "IDENT", "STRING", "SELECT", "FROM", "WHERE", "AND", "ASTERISCO", "EQUALS", "PONTO_VIRGULA"
  };

  private Yylex lexer;
  public ParserVal yylval;
  private static int laToken;
  private boolean debug;

  public AsdrSample(Reader r) {
    lexer = new Yylex(r, this);
  }

  private void Prog() {
    if (laToken == SELECT) {
      if (debug) System.out.println("Prog -> SELECT ListaColunas FROM Tabela CondicaoOpcional ;");
      verifica(SELECT);
      ListaColunas();
      verifica(FROM);
      Tabela();
      CondicaoOpcional();
      verifica(PONTO_VIRGULA);
    } else {
      yyerror("Esperado SELECT no início da consulta.");
    }
  }

  private void ListaColunas() {
    if (laToken == ASTERISCO) {
      if (debug) System.out.println("ListaColunas -> *");
      verifica(ASTERISCO);
    } else if (laToken == IDENT) {
      if (debug) System.out.println("ListaColunas -> Coluna ListaColunas'");
      Coluna();
      ListaColunasLinha();
    } else {
      yyerror("Esperado '*' ou identificador na lista de colunas.");
    }
  }

  private void ListaColunasLinha() {
    if (laToken == ',') {
      if (debug) System.out.println("ListaColunasLinha -> , Coluna ListaColunasLinha");
      verifica(',');
      Coluna();
      ListaColunasLinha();
    } else {
      if (debug) System.out.println("ListaColunasLinha -> (vazio)");
    }
  }

  private void Coluna() {
    if (laToken == IDENT) {
      if (debug) System.out.println("Coluna -> IDENT");
      verifica(IDENT);
    } else {
      yyerror("Esperado identificador em Coluna");
    }
  }

  private void Tabela() {
    if (laToken == IDENT) {
      if (debug) System.out.println("Tabela -> IDENT");
      verifica(IDENT);
    } else {
      yyerror("Esperado identificador em Tabela");
    }
  }

  private void CondicaoOpcional() {
    if (laToken == WHERE) {
      if (debug) System.out.println("CondicaoOpcional -> WHERE Condicao");
      verifica(WHERE);
      Condicao();
    } else {
      if (debug) System.out.println("CondicaoOpcional -> (vazio)");
    }
  }

  private void Condicao() {
    if (debug) System.out.println("Condicao -> Coluna = STRING CondicaoLinha");
    Coluna();
    verifica(EQUALS);
    if (laToken == STRING) {
      verifica(STRING);
      CondicaoLinha();
    } else {
      yyerror("Esperado valor string após '=' em Condicao");
    }
  }

  private void CondicaoLinha() {
    if (laToken == AND) {
      if (debug) System.out.println("CondicaoLinha -> && Condicao");
      verifica(AND);
      Condicao();
    } else {
      if (debug) System.out.println("CondicaoLinha -> (vazio)");
    }
  }

  private void verifica(int expected) {
      if (laToken == expected)
         laToken = this.yylex();
      else {
         String expStr, laStr;       

		expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         
		laStr = ((laToken < BASE_TOKEN_NUM )
                ? Character.toString(laToken)
                : tokenList[laToken-BASE_TOKEN_NUM]);

          yyerror( "esperado token: " + expStr +
                   " na entrada: " + laStr);
     }
   }

   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug = true;
  }

   public static void main(String[] args) {
     AsdrSample parser = null;
     try {
         if (args.length == 0)
            parser = new AsdrSample(new InputStreamReader(System.in));
         else 
            parser = new  AsdrSample( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
  }
}
