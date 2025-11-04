// Andador bem simples
// Lê path.txt e tenta encontrar o seu caminho até o caracter 'S'.
// [mvfm]

// Criado : 04/11/2025  ||  Última vez alterado : 04/11/2025

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Nodo{
    // Seu "valor", caracter que ele guarda.
    String valor;

    // Sua "posição."
    int x, y;

    int caminhoPercorrido, quantoFalta, custoTotal;

    // Nodo anterior do caminho.
    Nodo parente;

    Nodo(int x, int y, String valor){
        this.x = x;
        this.y = y;
        this.valor = valor;
    }
}

public class Walker {

    // Único método por enquanto, vai servir para enviar a list principal para o walker.
    public static List<Nodo> leTxt(String caminhoTxt){

        try(BufferedReader buff = new BufferedReader(new FileReader(caminhoTxt));){

            int dim = Integer.parseInt(buff.readLine());
            System.out.println("Dimensões da tabela : " + dim + "x" + dim);

            ArrayList<Nodo> tabela = new ArrayList<>();

            for (int i = 0; i < dim; i++){
                String linha = buff.readLine();

                if(linha == null){
                    throw new RuntimeException("Arquivo tem menos linhas do que o esperado.");
                }
                for (int j = 0; j < dim; j++){
                    // Cria Nodo, o adiciona na lista mantendo sua posição & o caracter encontrado no arquivo txt.
                    tabela.add(new Nodo(i, j, String.valueOf(linha.charAt(j))));
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
        List<Nodo> tabela = leTxt("path.txt");

        // Por já sabemos que a tabela resultante vai ser NxN.
        int dim = (int) Math.sqrt(tabela.size()); 
        System.out.println("Dimensões da tabela : " + dim + "x" + dim);

        if(tabela != null){
            for (int i = 0; i < tabela.size(); i++){
                System.out.print(tabela.get(i).valor);
            
                if((i + 1) % dim == 0){
                    System.out.println();
                }
            }
        }
    }

}