package br.ufscar.dc.compiladores.alguma.semantico;
 
import java.util.Iterator;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.CmdAtribuicaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_constanteContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_variavelContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.IdentificadorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Tipo_basico_identContext;

public class LASemantico extends LABaseVisitor<Object> {

    Escopo escopos = new Escopo();

    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        Iterator<LAParser.CmdContext> iterator = ctx.cmd().iterator();

        // Cria o escopo da função principal
        while (iterator.hasNext()) {
            LAParser.CmdContext cmd = iterator.next();
            if (cmd.cmdRetorne() != null) {
                Auxiliar.adicionarErroSemantico(cmd.getStart(), "comando retorne nao permitido nesse escopo");
            }
        }

        return super.visitCorpo(ctx);
    }

    // Obtém o tipo a partir de uma string
    @Override
    public Object visitDeclaracao_constante(Declaracao_constanteContext ctx) {
        TabelaDeSimbolos atual = escopos.obterEscopoAtual();
        if (atual.possui(ctx.IDENT().getText())) {
            Auxiliar.adicionarErroSemantico(ctx.start, "constante" + ctx.IDENT().getText()
                    + " ja declarado anteriormente");
        } else {
            EntradaTabelaDeSimbolos.Tipos tipo = EntradaTabelaDeSimbolos.Tipos.INT;
            switch (ctx.tipo_basico().getText()) {
                case "logico":
                    tipo = EntradaTabelaDeSimbolos.Tipos.LOGICO;
                    break;
                case "literal":
                    tipo = EntradaTabelaDeSimbolos.Tipos.CADEIA;
                    break;
                case "real":
                    tipo = EntradaTabelaDeSimbolos.Tipos.REAL;
                    break;
                case "inteiro":
                    tipo = EntradaTabelaDeSimbolos.Tipos.INT;
                    break;

            }
            atual.inserir(ctx.IDENT().getText(), tipo);
        }

        return super.visitDeclaracao_constante(ctx);
    }

    // Verifica se a variavel ja existe, se não existe, ele adiciona a variavel e seu tipo à tabela
    @Override
    public Object visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        TabelaDeSimbolos atual = escopos.obterEscopoAtual();
        Iterator<IdentificadorContext> iterator = ctx.variavel().identificador().iterator();

        while (iterator.hasNext()) {
            IdentificadorContext id = iterator.next();
            if (atual.possui(id.getText())) {
                Auxiliar.adicionarErroSemantico(id.start, "identificador " + id.getText()
                        + " ja declarado anteriormente");
            } else {
                EntradaTabelaDeSimbolos.Tipos tipo = EntradaTabelaDeSimbolos.Tipos.INT;
                switch (ctx.variavel().tipo().getText()) {
                    case "literal":
                        tipo = EntradaTabelaDeSimbolos.Tipos.CADEIA;
                        break;
                    case "inteiro":
                        tipo = EntradaTabelaDeSimbolos.Tipos.INT;
                        break;
                    case "real":
                        tipo = EntradaTabelaDeSimbolos.Tipos.REAL;
                        break;
                    case "logico":
                        tipo = EntradaTabelaDeSimbolos.Tipos.LOGICO;
                        break;
                }
                atual.inserir(id.getText(), tipo);
            }
        }
        return super.visitDeclaracao_variavel(ctx);
    }
    // Verifica se uma atribuição é válida e verifica se a expressão da atribuição tem o mesmo tipo que a variável ou é compatível com ela, se não for, emite erro
    @Override
    public Object visitDeclaracao_global(Declaracao_globalContext ctx) {
        TabelaDeSimbolos atual = escopos.obterEscopoAtual();
        if (atual.possui(ctx.IDENT().getText())) {
            Auxiliar.adicionarErroSemantico(ctx.start, ctx.IDENT().getText()
                    + " ja declarado anteriormente");
        } else {
            atual.inserir(ctx.IDENT().getText(), EntradaTabelaDeSimbolos.Tipos.TIPO);
        }
        return super.visitDeclaracao_global(ctx);
    }

    // Verifica se o tipo básico identificado é válido e verifica se o identificador foi declarado em algum escopo anterior, se sim, emite erro
    public Object visitTipo_basico_ident(Tipo_basico_identContext contextoTB) {
        if (contextoTB.IDENT() != null) {
            Iterator<TabelaDeSimbolos> iterator = escopos.percorrerEscoposAninhados().iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                TabelaDeSimbolos escopo = iterator.next();
                if (escopo.possui(contextoTB.IDENT().getText())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Auxiliar.adicionarErroSemantico(contextoTB.start, "tipo " + contextoTB.IDENT().getText() + " nao declarado");
            }
        }
        return super.visitTipo_basico_ident(contextoTB);
    }

    // Verifica se a variavel foi declarada em algum escopo ligado anterior, se não, emite erro
    public Object visitIdentificador(IdentificadorContext contextoTB) {
        Iterator<TabelaDeSimbolos> iterator = escopos.percorrerEscoposAninhados().iterator();
        boolean IdentDec = false;

        while (iterator.hasNext()) {
            TabelaDeSimbolos escopos = iterator.next();
            if (escopos.possui(contextoTB.IDENT(0).getText())) {
                IdentDec = true;
                break;
            }
        }

        if (!IdentDec) {
            Auxiliar.adicionarErroSemantico(contextoTB.start,
                    "identificador " + contextoTB.IDENT(0).getText() + " nao declarado");
        }

        return super.visitIdentificador(contextoTB);
    }

    // Verifica se a atribuição é válida.Tb verifica se a expressão da atribuição tem o mesmo tipo que a variável ou é compatível com ela, se não for, emite erro
    @Override
    public Object visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        EntradaTabelaDeSimbolos.Tipos Exptipo = Auxiliar.verificarTipo(escopos, ctx.expressao());
        boolean erro = false;
        String var = ctx.identificador().getText();
        if (Exptipo != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
            Iterator<TabelaDeSimbolos> iterator = escopos.percorrerEscoposAninhados().iterator();
            while (iterator.hasNext()) {
                TabelaDeSimbolos escopo = iterator.next();
                if (escopo.possui(var)) {
                    EntradaTabelaDeSimbolos.Tipos tipoVariavel = Auxiliar.verificarTipo(escopos, var);
                    Boolean varNumeric = tipoVariavel == EntradaTabelaDeSimbolos.Tipos.REAL || tipoVariavel == EntradaTabelaDeSimbolos.Tipos.INT;
                    Boolean expNumeric = Exptipo == EntradaTabelaDeSimbolos.Tipos.REAL || Exptipo == EntradaTabelaDeSimbolos.Tipos.INT;
                    if (!(varNumeric && expNumeric) && tipoVariavel != Exptipo
                            && Exptipo != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                        erro = true;
                        break;
                    }
                }
            }
        } else {
            erro = true;
        }

        if (erro)
            Auxiliar.adicionarErroSemantico(ctx.identificador().start,
                    "atribuicao nao compativel para " + var);

        return super.visitCmdAtribuicao(ctx);
    }

}