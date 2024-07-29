package br.ufscar.dc.compiladores.alguma.semantico;

public class EntradaTabelaDeSimbolos {
    public enum Tipos{
        INT, REAL, CADEIA, LOGICO, INVALIDO, TIPO, IDENT
    }

    public String nome;
    public Tipos tipo;

    public EntradaTabelaDeSimbolos(String nome, Tipos tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
}
