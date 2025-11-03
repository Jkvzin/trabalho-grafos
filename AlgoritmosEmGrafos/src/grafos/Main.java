package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner; // Importa o Scanner para ler a entrada do console

/**
 * Classe principal para executar o Trabalho Prático 2.
 * Este é o "programa" executável que interage com o usuário
 * e chama os algoritmos.
 */
public class Main {

    // Cria uma instância estática (global) da sua classe de algoritmos.
    // 'static' significa que este objeto pertence à classe Main, e não a uma instância dela.
    private static MeusAlgoritmosEmGrafos algoritmos = new MeusAlgoritmosEmGrafos();
    
    // Cria um Scanner estático para ler a entrada do usuário (System.in = console)
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Ponto de entrada do programa (o "play" do Java).
     */
    public static void main(String[] args) {
        System.out.println("Trabalho Prático 2 - Projeto e Análise de Algoritmos");
        Grafo g = null; // O objeto do grafo começa como nulo

        // Loop principal do programa
        while (true) {
            
            // --- PASSO 1: CARREGAR O GRAFO ---
            // Se o grafo 'g' for nulo (na primeira vez ou se o usuário pediu para recarregar)...
            if (g == null) {
                g = carregarGrafoPeloUsuario(); // ...chama o método para carregar
                if (g == null) {
                    System.out.println("Encerrando o programa.");
                    break; // Sai do while(true) se o carregamento falhar
                }
            }

            // --- PASSO 2: MENU DE ALGORITMOS ---
            // Chama o método que mostra o menu.
            // 'querSair' vai receber 'true' se o usuário digitar 0, ou 'false' se digitar 9.
            boolean querSair = mostrarMenuAlgoritmos(g);
            
            if (querSair) {
                break; // Usuário digitou 0 (Sair)
            } else {
                g = null; // Usuário digitou 9 (Recarregar), seta 'g' como nulo
                         // para que o loop 'while' o carregue de novo.
            }
        }
        
        // Fecha o scanner antes de sair
        scanner.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Pede ao usuário o caminho do arquivo e o tipo de representação,
     * e então chama o 'carregarGrafo' da sua classe de algoritmos.
     * @return O objeto Grafo carregado, ou null se falhar.
     */
    private static Grafo carregarGrafoPeloUsuario() {
        try {
            System.out.println("\n--- Carregar Novo Grafo ---");
            System.out.print("Digite o caminho do arquivo (ex: Teste.txt): ");
            String path = scanner.nextLine();

            // Menu para o tipo de representação
            System.out.println("Escolha o tipo de representação:");
            System.out.println("1. Lista de Adjacência");
            System.out.println("2. Matriz de Adjacência");
            System.out.println("3. Matriz de Incidência");
            System.out.print("Opção [1]: ");
            
            String tipoInput = scanner.nextLine();
            int tipoInt = 1; // Padrão é 1 (Lista de Adjacência)
            if (!tipoInput.isEmpty()) { // Se o usuário não apertar Enter direto
                tipoInt = Integer.parseInt(tipoInput);
            }

            // Converte o número (1, 2, 3) para o tipo Enum
            TipoDeRepresentacao tipo;
            switch (tipoInt) {
                case 1: tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA; break;
                case 2: tipo = TipoDeRepresentacao.MATRIZ_DE_ADJACENCIA; break;
                case 3: tipo = TipoDeRepresentacao.MATRIZ_DE_INCIDENCIA; break;
                default:
                    System.out.println("Tipo inválido. Usando Lista de Adjacência (1).");
                    tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA;
            }

            // Chama o método da *sua* classe de algoritmos
            Grafo g = algoritmos.carregarGrafo(path, tipo);
            System.out.println(">>> Grafo carregado com " + g.numeroDeVertices() + " vértices e " + g.numeroDeArestas() + " arestas.");
            return g; // Retorna o grafo com sucesso

        } catch (Exception e) {
            // Se qualquer coisa falhar (Arquivo não encontrado, parsing, etc.)
            System.out.println("!!! Erro ao carregar o grafo: " + e.getMessage());
            return null; // Retorna nulo, o que fará o programa encerrar
        }
    }

    /**
     * Mostra o menu principal de algoritmos e trata a escolha do usuário.
     * @param g O grafo já carregado.
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
                        System.out.println("Executando DFS (em todos os componentes)...");
                        algoritmos.buscaEmProfundidade(g);
                        imprimirResultadosDFS(g); // Chama o helper de impressão
                        break;
                    case 2: // BFS
                        Vertice sBFS = pedirVertice(g, "inicial");
                        if (sBFS == null) break; // Usuário cancelou
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
                        int[] sccMap = algoritmos.getSccMap();
                        imprimirResultadosSCC(g, gReduzido, sccMap); // Chama o helper de impressão
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
                        return false; // Sinaliza para o main() que deve recarregar
                    case 0: // Sair
                        return true; // Sinaliza para o main() que deve sair
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
     * (Helper) Pede ao usuário um ID de vértice até que um válido seja digitado.
     * @param g O grafo, para saber o limite de vértices.
     * @param tipo O texto para mostrar ao usuário (ex: "inicial", "de ORIGEM (s)")
     * @return O objeto Vertice correspondente, ou null se algo der errado.
     */
    private static Vertice pedirVertice(Grafo g, String tipo) {
        while (true) {
            try {
                System.out.print("Digite o ID do vértice " + tipo + ": ");
                int id = Integer.parseInt(scanner.nextLine());
                if (id < 0 || id >= g.numeroDeVertices()) {
                    System.out.println("!!! Erro: Vértice com ID " + id + " não existe. Tente um valor entre 0 e " + (g.numeroDeVertices() - 1) + ".");
                } else {
                    return g.vertices().get(id); // Retorna o vértice
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
     * (Helper) Formata uma coleção de arestas para uma impressão bonita.
     */
    private static String formatarArestas(Collection<Aresta> arestas) {
        if (arestas == null || arestas.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[\n");
        for (Aresta a : arestas) {
             if (a == null) continue; // Segurança
            sb.append("  ( " + a.origem().id() + " -> " + a.destino().id() + " | Peso: " + a.peso() + " )\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * (Helper) Imprime o relatório completo do DFS (listas e tempos).
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
     * (Helper) Imprime o relatório completo do BFS (lista, distâncias e pais).
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

    /**
     * (Helper) Imprime o relatório completo do SCC (classificação, listas e grafo reduzido).
     */
    private static void imprimirResultadosSCC(Grafo gOriginal, Grafo gReduzido, int[] sccMap) {
        int numSCC = gReduzido.numeroDeVertices();
        System.out.println(">>> Total de Componentes encontrados: " + numSCC);

        // Classificação do Grafo
        if (numSCC == 1) {
            System.out.println("\nClassificação do Grafo: Fortemente Conexo (f-conexo)");
        } else {
            System.out.println("\nClassificação do Grafo: Não é Fortemente Conexo.");
        }

        // Mapeamento de Vértices para Componentes
        System.out.println("\n--- Componentes ---");
        
        // 1. Cria 'numSCC' listas vazias
        ArrayList<ArrayList<Vertice>> componentes = new ArrayList<>();
        for (int i = 0; i < numSCC; i++) {
            componentes.add(new ArrayList<Vertice>());
        }

        // 2. Preenche as listas usando o sccMap
        for (Vertice v : gOriginal.vertices()) {
            int componenteId = sccMap[v.id()];
            componentes.get(componenteId).add(v);
        }
        
        // 3. Imprime as listas
        for (int i = 0; i < numSCC; i++) {
            System.out.print("Componente " + i + ": { ");
            String prefixo = "";
            for (Vertice v : componentes.get(i)) {
                System.out.print(prefixo + v.id());
                prefixo = ", ";
            }
            System.out.println(" }");
        }

        // Impressão do Grafo Reduzido
        System.out.println("\n--- Grafo Reduzido (Adjacências) ---");
        try {
            for (Vertice u : gReduzido.vertices()) {
                System.out.print(u.id() + " -> [ ");
                String prefixo = "";
                for (Vertice v : gReduzido.adjacentesDe(u)) {
                    System.out.print(prefixo + v.id());
                    prefixo = ", ";
                }
                System.out.println(" ]");
            }
        } catch (Exception e) {
            System.out.println("Erro ao imprimir grafo reduzido: " + e.getMessage());
        }
    }
}