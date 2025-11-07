// GeraLabirintos simples garantindo caminho E->S
// [mvfm]

// Criado : 07/11/2025  ||  Última vez alterado : 07/11/2025

import java.io.*;
import java.util.*;

public class PathCreator {

    private static final Random rand = new Random();

    public static void criaCaminhos(int dim) {
        Scanner teclado = new Scanner(System.in);
        System.out.println("Digite como desejas nomear o arquivo [sem extensão] : ");
        String nomeArquivo = teclado.nextLine().trim();
        String caminhoArquivo = "paths/" + nomeArquivo + ".txt";

        char[][] lab = geraLabirinto(dim);

        // Não encontrei outra maneira a não ser for encadeado. Por favor tenha piedade e não crie labirintos 50x50.
        try (FileWriter escritor = new FileWriter(caminhoArquivo)) {
            escritor.write(dim + "\n");
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    escritor.write(lab[i][j]);
                }
                escritor.write("\n");
            }
            System.out.println("Labirinto criado com sucesso em : " + caminhoArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    private static char[][] geraLabirinto(int dim) {
        char[][] grid = new char[dim][dim];
        for (int i = 0; i < dim; i++) {
            Arrays.fill(grid[i], '1');
        }

        // Garantindo que 'E' sempre vai ser a primeira posição.
        grid[0][0] = 'E';

        // Cria caminho garantido até uma posição aleatória
        List<int[]> caminho = geraCaminho(dim);

        for (int[] pos : caminho) {
            grid[pos[0]][pos[1]] = '0';
        }

        // Define saída (S) como o último ponto do caminho
        int[] fim = caminho.get(caminho.size() - 1);
        grid[fim[0]][fim[1]] = 'S';

        // Preenche o restante de forma semi-aleatória para parecer "Acreditável".
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (grid[i][j] == '1' && rand.nextDouble() < 0.25) {
                    grid[i][j] = '0'; // abre alguns caminhos extras
                }
            }
        }

        return grid;
    }

    // Caminho aleatório garantido (random walk não cíclico)
    private static List<int[]> geraCaminho(int dim) {

        // Ironicamente, seguindo lógica parecida com o Walker.
        List<int[]> caminho = new ArrayList<>();
        boolean[][] visitado = new boolean[dim][dim];
        int x = 0, y = 0;
        caminho.add(new int[]{x, y});
        visitado[x][y] = true;

        // Cria um caminho não tão grande.
        int passos = rand.nextInt(dim * dim / 2) + dim;

        // Seguindo o limite de passos criado anteriormente, cuida para não se passar dos limites do labirinto & sobrescrever sobre posições já visitadas.
        for (int i = 0; i < passos; i++) {
            List<int[]> vizinhos = new ArrayList<>();
            if (x > 0 && !visitado[x - 1][y]) vizinhos.add(new int[]{x - 1, y});
            if (x < dim - 1 && !visitado[x + 1][y]) vizinhos.add(new int[]{x + 1, y});
            if (y > 0 && !visitado[x][y - 1]) vizinhos.add(new int[]{x, y - 1});
            if (y < dim - 1 && !visitado[x][y + 1]) vizinhos.add(new int[]{x, y + 1});

            if (vizinhos.isEmpty()) break;

            int[] prox = vizinhos.get(rand.nextInt(vizinhos.size()));
            x = prox[0];
            y = prox[1];
            visitado[x][y] = true;
            caminho.add(prox);
        }

        return caminho;
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        System.out.print("Digite a dimensão do labirinto: ");
        int dim = teclado.nextInt();
        criaCaminhos(dim);
    }
}
