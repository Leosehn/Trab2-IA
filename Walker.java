// Andador bem simples
// Lê path.txt e tenta encontrar o seu caminho até o caracter 'S'.
// [mvfm]

// Criado : 04/11/2025  ||  Última vez alterado : 04/11/2025

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Walker {

    // Único método por enquanto, vai servir para enviar a matriz principal para o walker.
    public static String[][] leTxt(String caminhoTxt){

        try(BufferedReader buff = new BufferedReader(new FileReader(caminhoTxt));){

            int dim = Integer.parseInt(buff.readLine());
            System.out.println("Dimensões da tabela : " + dim + "x" + dim);

            String[][] tabela = new String[dim][dim];

            for (int i = 0; i < dim; i++){
                String linha = buff.readLine();

                if(linha == null){
                    throw new RuntimeException("Arquivo tem menos linhas do que o esperado.");
                }
                for (int j = 0; j < dim; j++){
                    tabela[i][j] = String.valueOf(linha.charAt(j));
                }
            }

            return tabela;

        }
    
        catch(IOException e){
            System.out.println("Erro na leitura do arquivo." + e.getMessage());
            return null;
        }

    }

    //Simplesmente printando por enquanto a matriz que o 'leTxt' cria.
    public static void main(String[] args) {
        String[][] tabela = leTxt("path.txt");
        
        if(tabela != null){
            for (String[] linha : tabela){
                for (String c : linha){
                    System.out.print(c);
                }
                System.out.println();
            }
        }

    }

}