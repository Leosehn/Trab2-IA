// Arquivo para implementação de algoritmos de Simulated Annealing (SA)
// Implementação completa para descobrir a saída 'S' sem conhecimento prévio
// Com saída formatada conforme especificação do trabalho
// [mvfm]

// Criado : 10/11/2025  ||  Última vez alterado : 17/11/2025

import java.io.*;
import java.util.*;

public class Sa {
    
    private static int geracaoAtual = 0;
    private static List<String> logGeracoes = new ArrayList<>();
    private static String arquivoSaida = "saida_sa.txt";
    
    /**
     * Método principal que usa SA para DESCOBRIR a saída do labirinto
     * sem conhecimento prévio da posição de 'S'
     */
    public static List<Nodo> sA_descobrirSaida(List<Nodo> tabela, int dim) {
        Random rand = new Random();
        
        System.out.println("=== FASE 1: Simulated Annealing para DESCOBRIR a saída ===");
        
        // Começar da entrada (posição 0,0)
        Nodo entrada = tabela.get(0);
        
        // Solução inicial: caminho aleatório a partir da entrada
        List<Nodo> caminhoAtual = gerarCaminhoAleatorio(entrada, tabela, dim, rand);
        double custoAtual = avaliarCaminho(caminhoAtual, tabela, dim);
        
        List<Nodo> melhorCaminho = new ArrayList<>(caminhoAtual);
        double melhorCusto = custoAtual;
        Nodo saidaEncontrada = null;
        
        // Parâmetros do SA
        double temperatura = 1000.0;
        double taxaResfriamento = 0.995;
        double temperaturaMinima = 0.1;
        int iteracoesSemMelhora = 0;
        int maxIteracoesSemMelhora = 500;
        
        int iteracao = 0;
        geracaoAtual = 0;
        logGeracoes.clear();
        
        while (temperatura > temperaturaMinima && iteracoesSemMelhora < maxIteracoesSemMelhora) {
            iteracao++;
            geracaoAtual = iteracao;
            
            // Perturbar o caminho atual
            List<Nodo> caminhoVizinho = perturbarCaminho(caminhoAtual, tabela, dim, rand);
            
            if (caminhoVizinho == null) {
                temperatura *= taxaResfriamento;
                continue;
            }
            
            double novoCusto = avaliarCaminho(caminhoVizinho, tabela, dim);
            double delta = novoCusto - custoAtual;
            
            // Critério de aceitação do SA
            if (delta < 0 || rand.nextDouble() < Math.exp(-delta / temperatura)) {
                caminhoAtual = caminhoVizinho;
                custoAtual = novoCusto;
                iteracoesSemMelhora = 0;
                
                // Verificar se encontramos a saída 'S'
                Nodo ultimoNodo = caminhoAtual.get(caminhoAtual.size() - 1);
                if (ultimoNodo.valor.equals("S")) {
                    saidaEncontrada = ultimoNodo;
                    System.out.println("*** SAÍDA ENCONTRADA na geração " + geracaoAtual + "! ***");
                    System.out.println("Posição da saída: (" + saidaEncontrada.x + ", " + saidaEncontrada.y + ")");
                    
                    // Registrar encontro da saída
                    registrarGeracao(caminhoAtual, "SAÍDA ENCONTRADA");
                    melhorCaminho = new ArrayList<>(caminhoAtual);
                    break;
                }
                
                // Guardar se for o melhor até agora
                if (custoAtual < melhorCusto) {
                    melhorCaminho = new ArrayList<>(caminhoAtual);
                    melhorCusto = custoAtual;
                    
                    // Modo rápido: exibir a cada X gerações
                    if (geracaoAtual % 50 == 0) {
                        System.out.println("GERAÇÃO: " + geracaoAtual);
                        exibirMelhorCromossomoRapido(melhorCaminho, melhorCusto);
                        
                    }
                    registrarGeracao(melhorCaminho, null);
                    
                    // Modo detalhado: a cada 200 gerações
                    if (geracaoAtual % 200 == 0) {
                        exibirMelhorCromossomoDetalhado(melhorCaminho, melhorCusto, temperatura);
                    }
                }
            } else {
                iteracoesSemMelhora++;
            }


            temperatura *= taxaResfriamento;
        }
        
        // Se não encontrou 'S' no melhor caminho, verificar se está no caminho atual
        if (saidaEncontrada == null) {
            Nodo ultimoNodo = melhorCaminho.get(melhorCaminho.size() - 1);
            if (ultimoNodo.valor.equals("S")) {
                saidaEncontrada = ultimoNodo;
                System.out.println("Saída encontrada no melhor caminho!");
            }
        }
        
        if (saidaEncontrada == null) {
            System.out.println("SA não conseguiu encontrar a saída 'S'.");
            return null;
        }
        
        System.out.println("\n=== FASE 2: A* para encontrar o caminho ÓTIMO ===");
        System.out.println("Executando A* da entrada até a saída descoberta...");
        
        // Agora que conhecemos a saída, usar A* para encontrar o melhor caminho
        List<Nodo> caminhoOtimo = Walker.aEstrela(tabela, dim);
        
        if (caminhoOtimo != null) {
            System.out.println("\n=== COMPARAÇÃO FINAL ===");
            System.out.println("Caminho encontrado pelo SA: " + melhorCaminho.size() + " passos");
            System.out.println("Caminho otimizado pelo A*: " + caminhoOtimo.size() + " passos");
            System.out.println("Melhoria: " + (melhorCaminho.size() - caminhoOtimo.size()) + " passos");
            
            // Salvar ambos os caminhos no arquivo
            salvarResultados(melhorCaminho, caminhoOtimo, tabela, dim);
            
            return caminhoOtimo;
        }
        
        salvarResultados(melhorCaminho, null, tabela, dim);
        return melhorCaminho;
    }
    
    /**
     * Exibe o melhor cromossomo de forma rápida (modo resumido)
     */
    private static void exibirMelhorCromossomoRapido(List<Nodo> caminho, double aptidao) {
        StringBuilder sb = new StringBuilder("(Cromossomo) ");
        
        // Mostrar apenas primeiros e últimos nodos
        int limite = Math.min(10, caminho.size());
        for (int i = 0; i < limite; i++) {
            Nodo n = caminho.get(i);
            sb.append(n.x).append(" ").append(n.y).append(" ");
        }
        
        if (caminho.size() > limite) {
            sb.append("... ");
            Nodo ultimo = caminho.get(caminho.size() - 1);
            sb.append(ultimo.x).append(" ").append(ultimo.y);
        }
        
        sb.append(" - Caminho: ");
        sb.append(formatarCoordenadas(caminho, 5));
        sb.append(" - Aptidão: ").append(String.format("%.1f", -aptidao));
        
        System.out.println(sb.toString());
    }
    
    /**
     * Exibe o melhor cromossomo de forma detalhada
     */
    private static void exibirMelhorCromossomoDetalhado(List<Nodo> caminho, double aptidao, double temp) {
        System.out.println("\n--- DETALHAMENTO DA GERAÇÃO " + geracaoAtual + " ---");
        System.out.println("Temperatura: " + String.format("%.2f", temp));
        System.out.println("Melhor Cromossomo: ");
        
        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < caminho.size(); i++) {
            Nodo n = caminho.get(i);
            coords.append(n.x).append(" ").append(n.y);
            if (i < caminho.size() - 1) coords.append(" ");
        }
        System.out.println(coords.toString());
        
        System.out.println("Caminho completo: " + formatarCoordenadas(caminho, caminho.size()));
        System.out.println("Aptidão: " + String.format("%.1f", -aptidao));
        System.out.println("Tamanho do caminho: " + caminho.size() + " passos");
        
        // Verificar se chegou na saída
        Nodo ultimo = caminho.get(caminho.size() - 1);
        if (ultimo.valor.equals("S")) {
            System.out.println("*** CHEGOU NA SAÍDA 'S' ***");
        }
        System.out.println("-----------------------------------\n");
    }
    
    /**
     * Formata coordenadas para exibição: (x,y)
     */
    private static String formatarCoordenadas(List<Nodo> caminho, int limite) {
        StringBuilder sb = new StringBuilder();
        int max = Math.min(limite, caminho.size());
        
        for (int i = 0; i < max; i++) {
            Nodo n = caminho.get(i);
            sb.append("(").append(n.x).append(",").append(n.y).append(")");
            if (i < max - 1) sb.append("-");
        }
        
        if (caminho.size() > limite) {
            sb.append("-...-");
            Nodo ultimo = caminho.get(caminho.size() - 1);
            sb.append("(").append(ultimo.x).append(",").append(ultimo.y).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * Registra informações da geração para o arquivo de saída
     */
    private static void registrarGeracao(List<Nodo> caminho, String evento) {
        StringBuilder sb = new StringBuilder();
        sb.append("GERAÇÃO: ").append(geracaoAtual).append("\n");
        
        if (evento != null) {
            sb.append(evento).append("\n");
        }
        
        sb.append("(Cromossomo) ");
        for (Nodo n : caminho) {
            sb.append(n.x).append(" ").append(n.y).append(" ");
        }
        sb.append("- Caminho: ");
        sb.append(formatarCoordenadas(caminho, caminho.size()));
        sb.append(" - Aptidão: ").append(String.format("%.1f", -avaliarCaminho(caminho, null, 0)));
        sb.append("\n");
        
        logGeracoes.add(sb.toString());
    }
    
    /**
     * Salva os resultados no arquivo conforme formato especificado
     */
    private static void salvarResultados(List<Nodo> caminhoSA, List<Nodo> caminhoAEstrela, 
                                         List<Nodo> tabela, int dim) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoSaida))) {
            
            writer.println("========================================");
            writer.println("RESULTADOS DO SIMULATED ANNEALING");
            writer.println("========================================\n");
            
            // Log das gerações importantes
            writer.println("=== HISTÓRICO DE GERAÇÕES ===\n");
            for (String log : logGeracoes) {
                writer.println(log);
            }
            
            writer.println("\n========================================");
            writer.println("CAMINHO ENCONTRADO PELO SIMULATED ANNEALING");
            writer.println("========================================\n");
            
            if (caminhoSA != null) {
                writer.println("Tamanho do caminho: " + caminhoSA.size() + " passos");
                writer.println("Caminho completo (formato x y):");
                
                StringBuilder coords = new StringBuilder();
                for (int i = 0; i < caminhoSA.size(); i++) {
                    Nodo n = caminhoSA.get(i);
                    coords.append(n.x).append(" ").append(n.y);
                    if (i < caminhoSA.size() - 1) coords.append(" ");
                }
                writer.println(coords.toString());
                
                writer.println("\nCaminho completo (formato coordenadas):");
                writer.println(formatarCoordenadas(caminhoSA, caminhoSA.size()));
                
                // Exibir mapa com caminho do SA
                writer.println("\n=== MAPA COM CAMINHO DO SA ===");
                imprimirMapaComCaminho(writer, tabela, dim, caminhoSA, "$");
            }
            
            if (caminhoAEstrela != null) {
                writer.println("\n========================================");
                writer.println("CAMINHO OTIMIZADO PELO A*");
                writer.println("========================================\n");
                
                writer.println("Tamanho do caminho: " + caminhoAEstrela.size() + " passos");
                writer.println("Caminho completo (formato x y):");
                
                StringBuilder coords = new StringBuilder();
                for (int i = 0; i < caminhoAEstrela.size(); i++) {
                    Nodo n = caminhoAEstrela.get(i);
                    coords.append(n.x).append(" ").append(n.y);
                    if (i < caminhoAEstrela.size() - 1) coords.append(" ");
                }
                writer.println(coords.toString());
                
                writer.println("\nCaminho completo (formato coordenadas):");
                writer.println(formatarCoordenadas(caminhoAEstrela, caminhoAEstrela.size()));
                
                // Exibir mapa com caminho do A*
                writer.println("\n=== MAPA COM CAMINHO OTIMIZADO (A*) ===");
                imprimirMapaComCaminho(writer, tabela, dim, caminhoAEstrela, "*");
                
                // Comparação
                writer.println("\n========================================");
                writer.println("COMPARAÇÃO");
                writer.println("========================================");
                writer.println("Caminho SA: " + caminhoSA.size() + " passos");
                writer.println("Caminho A*: " + caminhoAEstrela.size() + " passos");
                writer.println("Otimização: " + (caminhoSA.size() - caminhoAEstrela.size()) + " passos reduzidos");
            }
            
            writer.println("\n========================================");
            writer.println("Arquivo gerado com sucesso!");
            writer.println("========================================");
            
            System.out.println("\nResultados salvos em: " + arquivoSaida);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Imprime o mapa com o caminho marcado
     */
    private static void imprimirMapaComCaminho(PrintWriter writer, List<Nodo> tabela, 
                                               int dim, List<Nodo> caminho, String marcador) {
        // Criar matriz com valores originais
        String[][] matriz = new String[dim][dim];
        for (Nodo n : tabela) {
            matriz[n.x][n.y] = n.valor;
        }
        
        // Marcar o caminho
        for (Nodo n : caminho) {
            if (!n.valor.equals("S") && !n.valor.equals("E")) {
                matriz[n.x][n.y] = marcador;
            }
        }
        
        // Imprimir
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                writer.print(matriz[i][j]);
                if (j < dim - 1) writer.print(" ");
            }
            writer.println();
        }
    }
    
    /**
     * Função heurística para avaliar a qualidade de um caminho
     * Quanto MENOR o custo, MELHOR o caminho
     */
    private static double avaliarCaminho(List<Nodo> caminho, List<Nodo> tabela, int dim) {
        if (caminho == null || caminho.isEmpty()) {
            return Double.MAX_VALUE;
        }
        
        double custo = 0.0;
        Nodo ultimoNodo = caminho.get(caminho.size() - 1);
        
        // 1. Penalidade pelo tamanho do caminho (queremos caminhos curtos)
        custo += caminho.size() * 1.0;
        
        // 2. RECOMPENSA ENORME se encontrar a saída 'S'
        if (ultimoNodo.valor.equals("S")) {
            custo -= 10000.0; // Recompensa massiva por encontrar 'S'
        } else if (tabela != null) {
            // 3. Se não achou 'S', penalizar pela distância até áreas não exploradas
            double distanciaMedia = calcularDistanciaParaAreasInexploradas(ultimoNodo, tabela, dim);
            custo += distanciaMedia * 5.0;
        }
        
        // 4. Bonificação por explorar novos territórios (diversidade)
        Set<String> posicoesUnicas = new HashSet<>();
        for (Nodo n : caminho) {
            posicoesUnicas.add(n.x + "," + n.y);
        }
        custo -= posicoesUnicas.size() * 2.0; // Incentivar exploração
        
        // 5. Penalidade por loops (visitar mesma célula múltiplas vezes)
        int loops = caminho.size() - posicoesUnicas.size();
        custo += loops * 10.0;
        
        return custo;
    }
    
    /**
     * Calcula distância média para células não visitadas (incentiva exploração)
     */
    private static double calcularDistanciaParaAreasInexploradas(Nodo nodo, List<Nodo> tabela, int dim) {
        // Distância até os cantos do labirinto (áreas potencialmente não exploradas)
        int distCantoInfDir = Math.abs(nodo.x - (dim-1)) + Math.abs(nodo.y - (dim-1));
        int distCantoInfEsq = Math.abs(nodo.x - (dim-1)) + Math.abs(nodo.y - 0);
        int distCantoSupDir = Math.abs(nodo.x - 0) + Math.abs(nodo.y - (dim-1));
        
        return Math.min(distCantoInfDir, Math.min(distCantoInfEsq, distCantoSupDir));
    }
    
    /**
     * Gera um caminho aleatório inicial
     */
    private static List<Nodo> gerarCaminhoAleatorio(Nodo inicio, List<Nodo> tabela, int dim, Random rand) {
        List<Nodo> caminho = new ArrayList<>();
        caminho.add(inicio);
        
        Nodo atual = inicio;
        Set<String> visitados = new HashSet<>();
        visitados.add(atual.x + "," + atual.y);
        
        int maxPassos = dim * dim / 2; // Limitar tamanho inicial
        
        for (int i = 0; i < maxPassos; i++) {
            List<Nodo> vizinhos = Walker.getVizinhos(atual, tabela, dim);
            
            if (vizinhos.isEmpty()) break;
            
            // Escolher vizinho aleatório, preferindo não visitados
            List<Nodo> naoVisitados = new ArrayList<>();
            for (Nodo v : vizinhos) {
                if (!visitados.contains(v.x + "," + v.y)) {
                    naoVisitados.add(v);
                }
            }
            
            Nodo proximo;
            if (!naoVisitados.isEmpty() && rand.nextDouble() > 0.3) {
                proximo = naoVisitados.get(rand.nextInt(naoVisitados.size()));
            } else if (!vizinhos.isEmpty()) {
                proximo = vizinhos.get(rand.nextInt(vizinhos.size()));
            } else {
                break;
            }
            
            caminho.add(proximo);
            visitados.add(proximo.x + "," + proximo.y);
            atual = proximo;
            
            // Se encontrou 'S', parar
            if (atual.valor.equals("S")) {
                break;
            }
        }
        
        return caminho;
    }
    
    /**
     * Perturba o caminho atual para gerar uma solução vizinha
     */
    private static List<Nodo> perturbarCaminho(List<Nodo> caminho, List<Nodo> tabela, int dim, Random rand) {
        if (caminho.size() < 2) return null;
        
        int tipoMutacao = rand.nextInt(4);
        
        switch (tipoMutacao) {
            case 0: // Estender o caminho
                return estenderCaminho(caminho, tabela, dim, rand);
            
            case 1: // Modificar segmento intermediário
                return modificarSegmento(caminho, tabela, dim, rand);
            
            case 2: // Remover loop (otimização local)
                return removerLoop(caminho);
            
            case 3: // Substituir trecho por caminho alternativo
                return substituirTrecho(caminho, tabela, dim, rand);
            
            default:
                return estenderCaminho(caminho, tabela, dim, rand);
        }
    }
    
    /**
     * Estende o caminho com novos passos aleatórios
     */
    private static List<Nodo> estenderCaminho(List<Nodo> caminho, List<Nodo> tabela, int dim, Random rand) {
        List<Nodo> novoCaminho = new ArrayList<>(caminho);
        Nodo ultimo = novoCaminho.get(novoCaminho.size() - 1);
        
        // Se já encontrou 'S', não estender
        if (ultimo.valor.equals("S")) {
            return null;
        }
        
        // Adicionar alguns passos aleatórios
        int passos = 1 + rand.nextInt(5);
        Nodo atual = ultimo;
        
        for (int i = 0; i < passos; i++) {
            List<Nodo> vizinhos = Walker.getVizinhos(atual, tabela, dim);
            if (vizinhos.isEmpty()) break;
            
            Nodo proximo = vizinhos.get(rand.nextInt(vizinhos.size()));
            novoCaminho.add(proximo);
            atual = proximo;
            
            if (atual.valor.equals("S")) break;
        }
        
        return novoCaminho;
    }
    
    /**
     * Modifica um segmento intermediário do caminho
     */
    private static List<Nodo> modificarSegmento(List<Nodo> caminho, List<Nodo> tabela, int dim, Random rand) {
        if (caminho.size() < 3) return null;
        
        int pontoCorte = 1 + rand.nextInt(caminho.size() - 1);
        List<Nodo> novoCaminho = new ArrayList<>(caminho.subList(0, pontoCorte));
        
        Nodo pontoPartida = novoCaminho.get(novoCaminho.size() - 1);
        
        // Criar novo segmento a partir daqui
        int passos = 3 + rand.nextInt(10);
        Nodo atual = pontoPartida;
        
        for (int i = 0; i < passos; i++) {
            List<Nodo> vizinhos = Walker.getVizinhos(atual, tabela, dim);
            if (vizinhos.isEmpty()) break;
            
            Nodo proximo = vizinhos.get(rand.nextInt(vizinhos.size()));
            novoCaminho.add(proximo);
            atual = proximo;
            
            if (atual.valor.equals("S")) break;
        }
        
        return novoCaminho;
    }
    
    /**
     * Remove loops do caminho (otimização)
     */
    private static List<Nodo> removerLoop(List<Nodo> caminho) {
        if (caminho.size() < 3) return null;
        
        Map<String, Integer> primeiraOcorrencia = new HashMap<>();
        
        for (int i = 0; i < caminho.size(); i++) {
            Nodo n = caminho.get(i);
            String chave = n.x + "," + n.y;
            
            if (primeiraOcorrencia.containsKey(chave)) {
                // Encontrou loop! Remover o segmento entre as duas ocorrências
                int inicio = primeiraOcorrencia.get(chave);
                List<Nodo> novoCaminho = new ArrayList<>();
                novoCaminho.addAll(caminho.subList(0, inicio + 1));
                novoCaminho.addAll(caminho.subList(i + 1, caminho.size()));
                return novoCaminho;
            }
            
            primeiraOcorrencia.put(chave, i);
        }
        
        return null; // Nenhum loop encontrado
    }
    
    /**
     * Substitui um trecho do caminho por alternativa
     */
    private static List<Nodo> substituirTrecho(List<Nodo> caminho, List<Nodo> tabela, int dim, Random rand) {
        if (caminho.size() < 4) return null;
        
        int inicio = 1 + rand.nextInt(caminho.size() - 2);
        int tamanho = 1 + rand.nextInt(Math.min(5, caminho.size() - inicio - 1));
        int fim = Math.min(inicio + tamanho, caminho.size() - 1);
        
        Nodo pontoInicio = caminho.get(inicio);
        Nodo pontoFim = caminho.get(fim);
        
        // Tentar conectar usando busca simples
        List<Nodo> segmentoNovo = encontrarSegmento(pontoInicio, pontoFim, tabela, dim, rand);
        
        if (segmentoNovo != null && segmentoNovo.size() > 0) {
            List<Nodo> novoCaminho = new ArrayList<>();
            novoCaminho.addAll(caminho.subList(0, inicio));
            novoCaminho.addAll(segmentoNovo);
            novoCaminho.addAll(caminho.subList(fim, caminho.size()));
            return novoCaminho;
        }
        
        return null;
    }
    
    /**
     * Encontra segmento entre dois pontos usando busca gulosa simplificada
     */
    private static List<Nodo> encontrarSegmento(Nodo inicio, Nodo fim, List<Nodo> tabela, int dim, Random rand) {
        List<Nodo> segmento = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        
        Nodo atual = inicio;
        visitados.add(atual.x + "," + atual.y);
        
        int maxPassos = 20;
        
        for (int i = 0; i < maxPassos; i++) {
            if (atual.equals(fim)) {
                segmento.add(atual);
                return segmento;
            }
            
            segmento.add(atual);
            
            List<Nodo> vizinhos = Walker.getVizinhos(atual, tabela, dim);
            if (vizinhos.isEmpty()) return null;
            
            // Ordenar vizinhos por distância até o fim
            vizinhos.sort((a, b) -> {
                int distA = Walker.manhattan(a, fim);
                int distB = Walker.manhattan(b, fim);
                return Integer.compare(distA, distB);
            });
            
            // Escolher um dos melhores vizinhos (com alguma aleatoriedade)
            Nodo proximo = null;
            for (Nodo v : vizinhos) {
                String chave = v.x + "," + v.y;
                if (!visitados.contains(chave)) {
                    proximo = v;
                    break;
                }
            }
            
            if (proximo == null) {
                // Todos visitados, escolher qualquer um
                proximo = vizinhos.get(0);
            }
            
            visitados.add(proximo.x + "," + proximo.y);
            atual = proximo;
        }
        
        return null; // Não conseguiu conectar
    }
    
    /**
     * Método main para testar o SA
     */
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        System.out.println("Por favor, especifique o caminho do arquivo txt:");

        String path = teclado.nextLine();
        List<Nodo> tabela = Walker.leTxt(path);
        teclado.close();

        if (tabela == null) {
            System.out.println("Erro ao ler o arquivo.");
            return;
        }

        int dim = (int) Math.sqrt(tabela.size());

        System.out.println("\nLabirinto original:");
        for (int i = 0; i < tabela.size(); i++) {
            System.out.print(Walker.colorir(tabela.get(i).valor));
            if ((i + 1) % dim == 0) {
                System.out.println();
            }
        }

        // Executar SA para descobrir a saída e depois otimizar com A*
        List<Nodo> caminhoFinal = sA_descobrirSaida(tabela, dim);
        
        if (caminhoFinal != null) {
            System.out.println("\n=== RESULTADO FINAL ===");
            System.out.println("Caminho encontrado com " + caminhoFinal.size() + " passos:");
            
            System.out.println("\nSequência de coordenadas:");
            for (int i = 0; i < caminhoFinal.size(); i++) {
                Nodo n = caminhoFinal.get(i);
                if (i % 10 == 0 && i > 0) System.out.println();
                System.out.print("(" + n.x + "," + n.y + ")");
                if (i < caminhoFinal.size() - 1) System.out.print("-");
            }
            System.out.println();
            
            Walker.imprimeCaminhoIdeal(tabela, dim, caminhoFinal);
        } else {
            System.out.println("\nNenhum caminho encontrado até a saída.");
        }
    }
}