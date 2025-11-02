package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner; // Importa o Scanner para ler a entrada

/**
 * Classe principal para executar o Trabalho Prático 2.
 * Este é o "programa" que interage com o usuário.
 * * @author João (e Gemini)
 */
public class Main {

    // Instância da sua classe de algoritmos
    private static MeusAlgoritmosEmGrafos algoritmos = new MeusAlgoritmosEmGrafos();
    // Leitor de entrada do console
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Ponto de entrada do programa
     */
    public static void main(String[] args) {
        System.out.println("Trabalho Prático 2 - Projeto e Análise de Algoritmos");
        Grafo g = null;

        // Loop principal: 1. Carrega o grafo, 2. Mostra o menu
        while (true) {
            
            // --- PASSO 1: CARREGAR O GRAFO ---
            if (g == null) {
                g = carregarGrafoPeloUsuario();
                if (g == null) {
                    System.out.println("Encerrando o programa.");
                    break; // Sai do loop principal se o carregamento falhar
                }
            }

            // --- PASSO 2: MENU DE ALGORITMOS ---
            boolean querSair = mostrarMenuAlgoritmos(g);
            
            if (querSair) {
                break; // Sai do loop principal (usuário escolheu "Sair")
            } else {
                g = null; // Sinaliza para "Recarregar" o grafo
            }
        }
        
        scanner.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Pede ao usuário o caminho e o tipo, e carrega o grafo.
     */
    private static Grafo carregarGrafoPeloUsuario() {
        try {
            System.out.println("\n--- Carregar Novo Grafo ---");
            System.out.print("Digite o caminho do arquivo (ex: Teste.txt): ");
            String path = scanner.nextLine();

            System.out.println("Escolha o tipo de representação:");
            System.out.println("1. Lista de Adjacência");
            System.out.println("2. Matriz de Adjacência");
            System.out.println("3. Matriz de Incidência");
            System.out.print("Opção [1]: ");
            
            String tipoInput = scanner.nextLine();
            int tipoInt = 1; // Padrão é 1
            if (!tipoInput.isEmpty()) {
                tipoInt = Integer.parseInt(tipoInput);
            }

            TipoDeRepresentacao tipo;
            switch (tipoInt) {
                case 1: tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA; break;
                case 2: tipo = TipoDeRepresentacao.MATRIZ_DE_ADJACENCIA; break;
                case 3: tipo = TipoDeRepresentacao.MATRIZ_DE_INCIDENCIA; break;
                default:
                    System.out.println("Tipo inválido. Usando Lista de Adjacência (1).");
                    tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA;
            }

            Grafo g = algoritmos.carregarGrafo(path, tipo);
            System.out.println(">>> Grafo carregado com " + g.numeroDeVertices() + " vértices e " + g.numeroDeArestas() + " arestas.");
            return g;

        } catch (Exception e) {
            System.out.println("!!! Erro ao carregar o grafo: " + e.getMessage());
            // e.printStackTrace();
            return null;
        }
    }

    /**
     * Mostra o menu principal de algoritmos e trata a escolha do usuário.
     * @return true se o usuário quer sair, false se quer recarregar.
     */
    private static boolean mostrarMenuAlgoritmos(Grafo g) {
        while (true) {
            System.out.println("\n--- MENU DE ALGORITMOS ---");
            System.out.println("1. Busca em Profundidade (DFS)");
            System.out.println("2. Busca em Largura (BFS)");
            System.out.println("3. Existe Ciclo?");
            System.out.println("4. Componentes Fortemente Conexos (SCC)");
            System.out.println("5. Árvore Geradora Mínima (Kruskal)");
            System.out.println("6. Caminho Mínimo (Dijkstra)");
            System.out.println("7. Fluxo Máximo (Edmonds-Karp)");
            System.out.println("----------------------------");
            System.out.println("9. Recarregar outro Grafo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int escolha = Integer.parseInt(scanner.nextLine());
                System.out.println(); // Pula linha

                switch (escolha) {
                    case 1: // DFS
                        // O PDF pede um vértice inicial, mas a interface não.
                        // O seu código roda em todos os vértices.
                        System.out.println("Executando DFS (em todos os componentes)...");
                        algoritmos.buscaEmProfundidade(g);
                        imprimirResultadosDFS(g); // Chama o helper de impressão
                        break;
                    case 2: // BFS
                        Vertice sBFS = pedirVertice(g, "inicial");
                        if (sBFS == null) break;
                        Collection<Aresta> arestasBFS = algoritmos.buscaEmLargura(g, sBFS);
                        System.out.println("BFS executado a partir de " + sBFS.id());
                        imprimirResultadosBFS(g, arestasBFS); // Chama o helper de impressão
                        break;
                    case 3: // Existe Ciclo
                        System.out.println("Verificando se existe ciclo...");
                        boolean ciclo = algoritmos.existeCiclo(g);
                        System.out.println(">>> Resultado: O grafo " + (ciclo ? "CONTÉM" : "NÃO CONTÉM") + " ciclo.");
                        break;
                    case 4: // SCC
                        System.out.println("Calculando Componentes Fortemente Conexos...");
                        Grafo gReduzido = algoritmos.componentesFortementeConexos(g);
                        System.out.println(">>> Resultado: Grafo reduzido gerado com " + gReduzido.numeroDeVertices() + " componentes.");
                        break;
                    case 5: // AGM
                        System.out.println("Calculando Árvore Geradora Mínima (Kruskal)...");
                        Collection<Aresta> agm = algoritmos.arvoreGeradoraMinima(g);
                        double custoAGM = algoritmos.custoDaArvoreGeradora(g, agm);
                        System.out.println(">>> Resultado: Custo total da AGM = " + custoAGM);
                        System.out.println("Arestas da AGM: " + formatarArestas(agm));
                        break;
                    case 6: // Caminho Mínimo
                        System.out.println("Calculando Caminho Mínimo (Dijkstra)...");
                        Vertice origem = pedirVertice(g, "de ORIGEM (s)");
                        if (origem == null) break;
                        Vertice destino = pedirVertice(g, "de DESTINO (t)");
                        if (destino == null) break;
                        
                        ArrayList<Aresta> caminho = algoritmos.caminhoMinimo(g, origem, destino);
                        double custoCaminho = algoritmos.custoDoCaminhoMinimo(g, caminho, origem, destino);
                        System.out.println(">>> Resultado: Custo total = " + custoCaminho);
                        System.out.println("Caminho: " + formatarArestas(caminho));
                        break;
                    case 7: // Fluxo Máximo
                        System.out.println("Calculando Fluxo Máximo (Edmonds-Karp)...");
                        Vertice sFluxo = pedirVertice(g, "de ORIGEM (s)");
                        if (sFluxo == null) break;
                        Vertice tFluxo = pedirVertice(g, "de DESTINO (t)");
                        if (tFluxo == null) break;
                        
                        double fluxo = algoritmos.fluxoMaximo(g, sFluxo, tFluxo);
                        System.out.println(">>> Resultado: Fluxo Máximo = " + fluxo);
                        break;
                    case 9: // Recarregar
                        return false; // Sinaliza para recarregar
                    case 0: // Sair
                        return true; // Sinaliza para sair
                    default:
                        System.out.println("!!! Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("!!! Erro: Por favor, digite um número válido.");
            } catch (Exception e) {
                System.out.println("!!! Ocorreu um erro ao executar o algoritmo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper para pedir um ID de vértice ao usuário e retorná-lo.
     */
    private static Vertice pedirVertice(Grafo g, String tipo) {
        while (true) {
            try {
                System.out.print("Digite o ID do vértice " + tipo + ": ");
                int id = Integer.parseInt(scanner.nextLine());
                if (id < 0 || id >= g.numeroDeVertices()) {
                    System.out.println("!!! Erro: Vértice com ID " + id + " não existe. Tente um valor entre 0 e " + (g.numeroDeVertices() - 1) + ".");
                } else {
                    return g.vertices().get(id);
                }
            } catch (NumberFormatException e) {
                System.out.println("!!! Erro: Por favor, digite um número (ID).");
            } catch (Exception e) {
                System.out.println("!!! Erro inesperado: " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Helper para formatar a saída das arestas.
     */
    private static String formatarArestas(Collection<Aresta> arestas) {
        if (arestas == null || arestas.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[\n");
        for (Aresta a : arestas) {
            sb.append("  ( " + a.origem().id() + " -> " + a.destino().id() + " | Peso: " + a.peso() + " )\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Helper para imprimir todos os resultados do DFS (puxando dos getters).
     */
    private static void imprimirResultadosDFS(Grafo g) {
        System.out.println("Arestas de Árvore: " + formatarArestas(algoritmos.arestasDeArvore(g)));
        System.out.println("Arestas de Retorno: " + formatarArestas(algoritmos.arestasDeRetorno(g)));
        System.out.println("Arestas de Avanço: " + formatarArestas(algoritmos.arestasDeAvanco(g)));
        System.out.println("Arestas de Cruzamento: " + formatarArestas(algoritmos.arestasDeCruzamento(g)));

        System.out.println("\n--- Tempos (d/f) ---");
        int[] d = algoritmos.getTempoDescobertaDFS();
        int[] f = algoritmos.getTempoFinalizacaoDFS();
        System.out.println("Vertice | d[v] | f[v]");
        for (Vertice v : g.vertices()) {
            System.out.printf("  %3d   | %3d  | %3d\n", v.id(), d[v.id()], f[v.id()]);
        }
    }
    
    /**
     * Helper para imprimir todos os resultados do BFS (puxando dos getters).
     */
    private static void imprimirResultadosBFS(Grafo g, Collection<Aresta> arestas) {
        System.out.println("Arestas da Árvore BFS: " + formatarArestas(arestas));

        System.out.println("\n--- Distâncias (d) e Pais (pi) ---");
        int[] d = algoritmos.getDistanciaBFS();
        Vertice[] pai = algoritmos.getPaiBFS();
        System.out.println("Vertice | d[v] (Dist) | pi[v] (Pai)");
        for (Vertice v : g.vertices()) {
            String paiStr = (pai[v.id()] == null) ? "null" : String.valueOf(pai[v.id()].id());
            String distStr = (d[v.id()] == Integer.MAX_VALUE) ? "inf" : String.valueOf(d[v.id()]);
            System.out.printf("  %3d   | %11s | %11s\n", v.id(), distStr, paiStr);
        }
    }
}