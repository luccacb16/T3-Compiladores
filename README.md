# T3-Compiladores

## Integrantes ##

### Lucas Abbiati Pereira, 801572 ###
### Lucca Couto Barberato, 800257 ###

Para conseguirmos executar o código é necessário que algumas dependências estejam em uma versão específica.

## Versões ##
+ Java: 1.8
+ Junit: 4.11
+ Antlr: 4.11.1
+ maven-clean-plugin: 3.1.0
+ maven-resources-plugin: 3.0.2
+ maven-compiler-plugin: 3.8.0
+ maven-surefire-plugin: 2.22.1
+ maven-jar-plugin: 3.0.2
+ maven-install-plugin: 2.5.2
+ maven-deploy-plugin: 2.8.2
+ maven-site-plugin: 3.7.1
+ maven-project-info-reports-plugin: 3.0.0

## 1° Opção de execução ##
Para buildar o projeto é necessário usar um comando na raiz do diretório

    mvn package

Após executar o comando anterior, irá criar um arquivo .jar. Para utilizar o corretor e testar o programa é necessário usar um comando com alguns parâmetros. A entrada é um arquivo .txt e a saída é salva em um arquivo .txt também
OBS: O arquivo dos casos teste foi disponibilizado pelo professor da disciplina Daniel Lucredio

    java -jar <caminho do arquivo alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies> <caminho para o txt de entrada> <caminho para o txt de saída>

## 2° Opção de execução ##
Os integrantes do grupo criaram um script com o nome run_corretor.sh que facilita o build e a execução do programa. Para rodar é necessário executar o script na raiz do diretório

    ./run_corretor.sh
    
Caso haja erro de permissão, é necessário outro comando para conceder permissão ao script

    chmod +x run_corretor.sh

## Resultado ##
O resultado final será exibido no arquivo de saída (o mesmo que foi passado como parâmetro 
para a execução)
    
