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

    int g,      // Caminho percorrido.
        h,      // Quanto falta.
        f;      // Custo total, (g + h).

    // Nodo anterior do caminho.
    Nodo parente;

    Nodo(int x, int y, String valor){
        this.x = x;
        this.y = y;
        this.valor = valor;
    }

    // Override do equals && hash para comparar nodos.
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Nodo nodo = (Nodo) obj;
        return x == nodo.x && y == nodo.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

}

public class Walker {

    // Cálculo de distância de Manhattan bem simples.
    public static int manhattan(Nodo atual, Nodo objetivo){
        return Math.abs(atual.x - objetivo.x) + Math.abs(atual.y - objetivo.y);
    }

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

    public static List<Nodo> getVizinhos(Nodo atual, List<Nodo> tabela, int dim){

        List<Nodo> vizinhos = new ArrayList<>();
        
        //Lista de movimentos possíveis para o andador. Codificado para 0's e 1's.
        int[][] movimentos = {
            {1,0},  // Baixo
            {-1,0}, // Cima
            {0, 1}, // Direita
            {0, -1} // Esquerda
        };

        for(int[] m : movimentos){
            int nx = atual.x + m[0];
            int ny = atual.y + m[1];

            // Verifica se está dentro da tabela.
            if(nx >= 0 && ny >= 0 && nx < dim && ny < dim){
                Nodo n = tabela.get(nx * dim + ny);

                // Vizinhos válidos são só aqueles de caminho livre ('E', '0' ou 'S')
                if(!n.valor.equals("1")){
                    vizinhos.add(n);
                }
            }
        }

        return vizinhos;

    }

    // Passa pela tabela e econtra o 'caminho de menor resistência'
    public static List<Nodo> aEstrela(List<Nodo> tabela, int dim){
        ArrayList<Nodo> caminhoEncontrado = new ArrayList<Nodo>();

        Nodo entrada = tabela.get(0);
        Nodo saida = null;

        System.out.println("Procurando por 'S'...");
        for(Nodo n : tabela){
            if(n.valor.equals("S")){
                System.out.println("'S' encontrado! Continuando com A*...");
                saida = n;
                break;
            }
        }

        if(saida == null){ throw new RuntimeException("Nenhum 'S' encontrado na tabela!"); };

        // PriorityQueue baseando-se no 'custoTotal' de um nodo.
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        HashSet<Nodo> closedSet = new HashSet<>();

        // Ainda não caminho nada, portanto g = 0
        entrada.g = 0;
        // Estabelecendo distância inicial entre entrada e saída. Manhattan funciona bem por não termos movimentos diagonais.
        entrada.h = manhattan(entrada, saida);
        // Custo total (g+h)
        entrada.f = entrada.h;

        openSet.add(entrada);

        while(!openSet.isEmpty()){
            Nodo atual = openSet.poll();

            if(atual.equals(saida)) {
                while(atual != null){
                    caminhoEncontrado.add(atual);
                    atual = atual.parente;
                }
                Collections.reverse(caminhoEncontrado);
                return caminhoEncontrado;
            }

            closedSet.add(atual);

            for(Nodo viz : getVizinhos(atual, tabela, dim)){
                if(closedSet.contains(viz)){ continue; }

                int gScore = atual.g + 1;
                boolean novoMelhor = false;

                if(!openSet.contains(viz)){
                    novoMelhor = true;
                } else if(gScore < viz.g){
                    novoMelhor = true;
                }

                if(novoMelhor){
                    viz.parente = atual;
                    viz.g = gScore;
                    viz.h = manhattan(viz, saida);
                    viz.f = viz.g + viz.h;
                    openSet.add(viz);
                }
            }
        }

        // Nenhum caminho encontrado.
        return null;

    }

    //Simplesmente printando por enquanto a matriz que o 'leTxt' cria.
    public static void main(String[] args) {
        List<Nodo> tabela = leTxt("path.txt");

        // Por já sabemos que a tabela resultante vai ser NxN.
        int dim = (int) Math.sqrt(tabela.size()); 

        if(tabela != null){
            for (int i = 0; i < tabela.size(); i++){
                System.out.print(tabela.get(i).valor);
            
                if((i + 1) % dim == 0){
                    System.out.println();
                }
            }
        }

        List<Nodo> caminho = aEstrela(tabela, dim);
        if(caminho != null){
            System.out.println("Caminho encontrado:");
            for(Nodo n : caminho){
                System.out.println("Nodo em (" + n.x + ", " + n.y + ") com valor '" + n.valor + "'");
            }
        } else {
            System.out.println("Nenhum caminho encontrado.");
        }
    }

}