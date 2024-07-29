package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.FatorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Fator_logicoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ParcelaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.TermoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Termo_logicoContext;

import java.util.Iterator;

public class Auxiliar {
    // Lista que armazena erros semânticos encontrados
    public static List<String> errosSemanticos = new ArrayList<>();

    // Adiciona um erro semântico à lista com a linha e mensagem especificada
    public static void adicionarErroSemantico(Token token, String mensagemErro) {
        int linhaErro = token.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linhaErro, mensagemErro));
    }

    // Determina o tipo de um símbolo usando a tabela de símbolos e uma expressão
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.ExpressaoContext contextoExpr) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
        Iterator<Termo_logicoContext> iterTermoLogico = contextoExpr.termo_logico().iterator();
        while (iterTermoLogico.hasNext()) {
            Termo_logicoContext termoLogicoAtual = iterTermoLogico.next();
            EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, termoLogicoAtual);
            if (tipoResultado == null) {
                tipoResultado = tipoAux;
            } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de um termo lógico usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Termo_logicoContext contextoTermoLogico) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
        Iterator<Fator_logicoContext> iterFatorLogico = contextoTermoLogico.fator_logico().iterator();
        while (iterFatorLogico.hasNext()) {
            Fator_logicoContext fatorLogicoAtual = iterFatorLogico.next();
            EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, fatorLogicoAtual);
            if (tipoResultado == null) {
                tipoResultado = tipoAux;
            } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de um fator lógico usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Fator_logicoContext contextoFatorLogico) {
        return verificarTipo(contextoEscopos, contextoFatorLogico.parcela_logica());
    }

    // Determina o tipo de uma parcela lógica usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Parcela_logicaContext contextoParcelaLogica) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
        if (contextoParcelaLogica.exp_relacional() != null) {
            tipoResultado = verificarTipo(contextoEscopos, contextoParcelaLogica.exp_relacional());
        } else {
            tipoResultado = EntradaTabelaDeSimbolos.Tipos.LOGICO;
        }
        return tipoResultado;
    }

    // Determina o tipo de uma expressão relacional usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Exp_relacionalContext contextoExprRel) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
        if (contextoExprRel.op_relacional() != null) {
            Iterator<Exp_aritmeticaContext> iterExpArit = contextoExprRel.exp_aritmetica().iterator();
            while (iterExpArit.hasNext()) {
                Exp_aritmeticaContext exprAritAtual = iterExpArit.next();
                EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, exprAritAtual);
                Boolean auxNumerico = tipoAux == EntradaTabelaDeSimbolos.Tipos.REAL || tipoAux == EntradaTabelaDeSimbolos.Tipos.INT;
                Boolean resultadoNumerico = tipoResultado == EntradaTabelaDeSimbolos.Tipos.REAL || tipoResultado == EntradaTabelaDeSimbolos.Tipos.INT;
                if (tipoResultado == null) {
                    tipoResultado = tipoAux;
                } else if (!(auxNumerico && resultadoNumerico) && tipoAux != tipoResultado) {
                    tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
                }
            }

            if (tipoResultado != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.LOGICO;
            }
        } else {
            tipoResultado = verificarTipo(contextoEscopos, contextoExprRel.exp_aritmetica(0));
        }
        return tipoResultado;
    }

    // Determina o tipo de uma expressão aritmética usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Exp_aritmeticaContext contextoExpArit) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
        Iterator<TermoContext> iterTermo = contextoExpArit.termo().iterator();
        while (iterTermo.hasNext()) {
            TermoContext termoAtual = iterTermo.next();
            EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, termoAtual);
            if (tipoResultado == null) {
                tipoResultado = tipoAux;
            } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de um termo usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.TermoContext contextoTermo) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;

        Iterator<FatorContext> iterFator = contextoTermo.fator().iterator();
        while (iterFator.hasNext()) {
            FatorContext fatorAtual = iterFator.next();
            EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, fatorAtual);
            Boolean auxNumerico = tipoAux == EntradaTabelaDeSimbolos.Tipos.REAL || tipoAux == EntradaTabelaDeSimbolos.Tipos.INT;
            Boolean resultadoNumerico = tipoResultado == EntradaTabelaDeSimbolos.Tipos.REAL || tipoResultado == EntradaTabelaDeSimbolos.Tipos.INT;
            if (tipoResultado == null) {
                tipoResultado = tipoAux;
            } else if (!(auxNumerico && resultadoNumerico) && tipoAux != tipoResultado) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de um fator usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.FatorContext contextoFator) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = null;

        Iterator<ParcelaContext> iterParcela = contextoFator.parcela().iterator();
        while (iterParcela.hasNext()) {
            ParcelaContext parcelaAtual = iterParcela.next();
            EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, parcelaAtual);
            if (tipoResultado == null) {
                tipoResultado = tipoAux;
            } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de uma parcela usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.ParcelaContext contextoParcela) {
        EntradaTabelaDeSimbolos.Tipos tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;

        if (contextoParcela.parcela_nao_unario() != null) {
            tipoResultado = verificarTipo(contextoEscopos, contextoParcela.parcela_nao_unario());
        } else {
            tipoResultado = verificarTipo(contextoEscopos, contextoParcela.parcela_unario());
        }
        return tipoResultado;
    }

    // Determina o tipo de uma parcela não unária usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Parcela_nao_unarioContext contextoParcelaNaoUnario) {
        if (contextoParcelaNaoUnario.identificador() != null) {
            return verificarTipo(contextoEscopos, contextoParcelaNaoUnario.identificador());
        }
        return EntradaTabelaDeSimbolos.Tipos.CADEIA;
    }

    // Determina o tipo de um identificador usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.IdentificadorContext contextoIdentificador) {
        String nomeVar = "";
        EntradaTabelaDeSimbolos.Tipos tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
        for (int i = 0; i < contextoIdentificador.IDENT().size(); i++) {
            nomeVar += contextoIdentificador.IDENT(i).getText();
            if (i != contextoIdentificador.IDENT().size() - 1) {
                nomeVar += ".";
            }
        }
        Iterator<TabelaDeSimbolos> iterTabela = contextoEscopos.percorrerEscoposAninhados().iterator();
        while (iterTabela.hasNext()) {
            TabelaDeSimbolos tabelaAtual = iterTabela.next();
            if (tabelaAtual.possui(nomeVar)) {
                tipoResultado = verificarTipo(contextoEscopos, nomeVar);
            }
        }
        return tipoResultado;
    }

    // Determina o tipo de uma parcela unária usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, LAParser.Parcela_unarioContext contextoParcelaUnario) {
        if (contextoParcelaUnario.NUM_INT() != null) {
            return EntradaTabelaDeSimbolos.Tipos.INT;
        }
        if (contextoParcelaUnario.NUM_REAL() != null) {
            return EntradaTabelaDeSimbolos.Tipos.REAL;
        }
        if (contextoParcelaUnario.identificador() != null) {
            return verificarTipo(contextoEscopos, contextoParcelaUnario.identificador());
        }
        if (contextoParcelaUnario.IDENT() != null) {
            EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
            tipoResultado = verificarTipo(contextoEscopos, contextoParcelaUnario.IDENT().getText());
            Iterator<ExpressaoContext> iterExpr = contextoParcelaUnario.expressao().iterator();
            while (iterExpr.hasNext()) {
                ExpressaoContext exprAtual = iterExpr.next();
                EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, exprAtual);
                if (tipoResultado == null) {
                    tipoResultado = tipoAux;
                } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                    tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
                }
            }
            return tipoResultado;
        } else {
            EntradaTabelaDeSimbolos.Tipos tipoResultado = null;
            Iterator<ExpressaoContext> iterExpr = contextoParcelaUnario.expressao().iterator();
            while (iterExpr.hasNext()) {
                ExpressaoContext exprAtual = iterExpr.next();
                EntradaTabelaDeSimbolos.Tipos tipoAux = verificarTipo(contextoEscopos, exprAtual);
                if (tipoResultado == null) {
                    tipoResultado = tipoAux;
                } else if (tipoResultado != tipoAux && tipoAux != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                    tipoResultado = EntradaTabelaDeSimbolos.Tipos.INVALIDO;
                }
            }
            return tipoResultado;
        }
    }

    // Determina o tipo de uma string usando a tabela de símbolos
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo contextoEscopos, String nomeVar) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;
        Iterator<TabelaDeSimbolos> iterTabela = contextoEscopos.percorrerEscoposAninhados().iterator();
        while (iterTabela.hasNext()) {
            TabelaDeSimbolos tabelaAtual = iterTabela.next();
            tipo = tabelaAtual.verificar(nomeVar);
        }
        return tipo;
    }
}