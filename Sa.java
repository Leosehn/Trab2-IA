// Arquivo para implementação de algoritmos de Simulated Annealing (SA)
// Vou estar aproveitando certos métodos do Walker para conveniência.
// [mvfm]

// Criado : 10/11/2025  ||  Última vez alterado : 10/11/2025

import java.io.*;
import java.util.*;

public class Sa {

    // Só com isso temos acesso para os métodos estáticos do Walker.
    Walker ajudante = new Walker();

    // Método principal da classe. Melhoras sucessivas dada uma proposta de solução.
    // Por enquanto, a proposta é bem ruim. Estamos assumindo que (0,0) é uma prosposta 'razoável'.
    public static List<Nodo> sA(List<Nodo> tabela, int dim){
        Random rand = new Random();

        Nodo inicio = tabela.get(0);
        Nodo objetivo = null;

        for(Nodo n : tabela){
            if(n.valor.equals("S")){
                objetivo = n;
                System.out.println("'S' encontrado em : " + "(" + n.x + "," + n.y + ")");
                break;
            }
        }

        if(objetivo == null){
            throw new RuntimeException("Nodo final 'S' não encontrado na tabela!");
        }

                Nodo atual = inicio;
        int custoAtual = Walker.manhattan(atual, objetivo);

        double temperatura = 100.0;   // temperatura inicial
        double taxaResfriamento = 0.99;
        double temperaturaMinima = 0.1;

        List<Nodo> caminho = new ArrayList<>();
        caminho.add(atual);

        while(temperatura > temperaturaMinima && !atual.equals(objetivo)){
            List<Nodo> vizinhos = Walker.getVizinhos(atual, tabela, dim);
            if(vizinhos.isEmpty()) break;

            Nodo proximo = vizinhos.get(rand.nextInt(vizinhos.size()));
            int novoCusto = Walker.manhattan(proximo, objetivo);
            int delta = novoCusto - custoAtual;

            if(delta < 0){
                atual = proximo;
                custoAtual = novoCusto;
                caminho.add(atual);
            } else {
                double prob = Math.exp(-delta / temperatura);
                if(rand.nextDouble() < prob){
                    atual = proximo;
                    custoAtual = novoCusto;
                    caminho.add(atual);
                }
            }

            System.out.printf("T: %.2f | Atual: (%d,%d) | Distância restante: %d%n",
                temperatura, atual.x, atual.y, custoAtual);

            temperatura *= taxaResfriamento;
        }

        if(atual.equals(objetivo)){
            System.out.println("Saída encontrada com SA!");
            return caminho;
        } else {
            System.out.println("SA finalizou sem encontrar a saída.");
            return null;
        }
 
    }
    
}
