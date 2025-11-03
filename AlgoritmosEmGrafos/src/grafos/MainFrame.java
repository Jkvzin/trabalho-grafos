package grafos;

// Importa todas as classes do Swing (para a GUI)
import javax.swing.*; 
// Importa as classes de layout e eventos (BorderLayout, FlowLayout, Font, etc.)
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Importa a classe de Arquivo (para o seletor de arquivos)
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
// (Scanner não é necessário na versão GUI)

/**
 * Interface Gráfica (GUI) principal para o Trabalho Prático 2.
 * Este arquivo é o programa executável (substitui o Main.java de console).
 * * @author João (e Gemini)
 */
public class MainFrame extends JFrame { // JFrame é a "Janela" principal

    // --- Componentes da GUI (os botões, menus, etc.) ---
    private JMenuBar menuBar;           // A barra de menu superior (Arquivo)
    private JMenu menuArquivo;          // O menu "Arquivo"
    private JMenuItem itemCarregarGrafo; // A opção "Carregar Grafo..."
    private JMenuItem itemSair;         // A opção "Sair"

    private JPanel painelControles;     // O painel superior com o dropdown e o botão
    private JComboBox<String> dropdownAlgoritmos; // O menu dropdown
    private JButton botaoExecutar;      // O botão "Executar"

    private JTextArea areaResultados;   // A caixa de texto grande para os resultados
    private JScrollPane scrollResultados; // A barra de rolagem para a caixa de texto

    // --- Variáveis de Lógica ---
    private MeusAlgoritmosEmGrafos algoritmos; // Sua classe de algoritmos
    private Grafo grafoCarregado;             // O grafo que está na memória

    /**
     * Construtor da nossa janela principal.
     * É executado quando o programa inicia.
     */
    public MainFrame() {
        // 1. Instancia sua classe de algoritmos (onde está o DFS, BFS, etc.)
        this.algoritmos = new MeusAlgoritmosEmGrafos();

        // 2. Configura a janela principal
        setTitle("Trabalho Prático 2 - Algoritmos em Grafos");
        setSize(800, 600); // Tamanho em pixels (largura, altura)
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Faz o programa fechar no 'X'
        setLayout(new BorderLayout()); // Define o layout (Norte, Sul, Centro, etc.)

        // 3. Chama os métodos auxiliares para criar os pedaços da GUI
        criarMenuBar();
        criarPainelControles();
        criarAreaResultados();

        // 4. Adiciona os componentes criados na janela
        this.setJMenuBar(menuBar); // Põe a barra de menu no topo
        this.add(painelControles, BorderLayout.NORTH); // Põe os controles no Norte (topo)
        this.add(scrollResultados, BorderLayout.CENTER); // Põe a área de texto no Centro
    }

    /**
     * (Helper) Cria a barra de menu (Arquivo -> Carregar, Sair)
     */
    private void criarMenuBar() {
        menuBar = new JMenuBar();
        menuArquivo = new JMenu("Arquivo");

        // Item "Carregar Grafo..."
        itemCarregarGrafo = new JMenuItem("Carregar Grafo...");
        // Define o que acontece quando o item é clicado
        itemCarregarGrafo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chama o nosso método de carregar o grafo
                onCarregarGrafo();
            }
        });
        menuArquivo.add(itemCarregarGrafo);

        menuArquivo.addSeparator(); // Adiciona uma linha cinza de separação

        // Item "Sair"
        itemSair = new JMenuItem("Sair");
        // Define a ação (lambda) para fechar o programa
        itemSair.addActionListener(e -> System.exit(0)); 
        menuArquivo.add(itemSair);

        menuBar.add(menuArquivo);
    }

    /**
     * (Helper) Cria o painel de controles (Dropdown e Botão "Executar")
     */
    private void criarPainelControles() {
        painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Define as opções que aparecerão no menu dropdown
        String[] nomesAlgoritmos = {
            "1. Busca em Profundidade (DFS)",
            "2. Busca em Largura (BFS)",
            "3. Existe Ciclo?",
            "4. Componentes Fortemente Conexos (SCC)",
            "5. Árvore Geradora Mínima (AGM)",
            "6. Caminho Mínimo (Dijkstra)",
            "7. Fluxo Máximo (Edmonds-Karp)"
        };
        dropdownAlgoritmos = new JComboBox<>(nomesAlgoritmos);
        
        // Cria o botão "Executar"
        botaoExecutar = new JButton("Executar");
        // Define o que acontece quando o botão é clicado
        botaoExecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chama o nosso método de executar o algoritmo
                onExecutarAlgoritmo();
            }
        });

        // Adiciona os componentes (rótulo, dropdown, botão) ao painel
        painelControles.add(new JLabel("Escolha o Algoritmo:"));
        painelControles.add(dropdownAlgoritmos);
        painelControles.add(botaoExecutar);

        // Começa tudo desabilitado, até o usuário carregar um grafo
        dropdownAlgoritmos.setEnabled(false);
        botaoExecutar.setEnabled(false);
    }

    /**
     * (Helper) Cria a área de texto onde os resultados serão impressos
     */
    private void criarAreaResultados() {
        areaResultados = new JTextArea("Por favor, carregue um grafo em (Arquivo -> Carregar Grafo...)\n");
        areaResultados.setEditable(false); // Impede o usuário de digitar
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Fonte boa para tabelas
        // Adiciona a área de texto a um painel com barra de rolagem
        scrollResultados = new JScrollPane(areaResultados);
    }

    // --- MÉTODOS DE LÓGICA (AÇÕES) ---

    /**
     * AÇÃO: Chamado quando o usuário clica em "Arquivo -> Carregar Grafo..."
     */
    private void onCarregarGrafo() {
        // 1. Abre uma janela do sistema para escolher um arquivo
        JFileChooser seletor = new JFileChooser("."); // Começa na pasta do projeto
        int resultado = seletor.showOpenDialog(this);

        // Se o usuário selecionou um arquivo e clicou "Abrir"
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = seletor.getSelectedFile();
            
            // 2. Pergunta o tipo de representação (em um popup)
            Object[] opcoes = {"Lista de Adjacência", "Matriz de Adjacência", "Matriz de Incidência"};
            int n = JOptionPane.showOptionDialog(this,
                "Como você quer armazenar este grafo?",
                "Escolha a Representação",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);

            TipoDeRepresentacao tipo;
            switch (n) {
                case 0: tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA; break;
                case 1: tipo = TipoDeRepresentacao.MATRIZ_DE_ADJACENCIA; break;
                case 2: tipo = TipoDeRepresentacao.MATRIZ_DE_INCIDENCIA; break;
                default: return; // Usuário fechou o popup (X)
            }

            // 3. Tenta carregar o grafo (chamando sua classe)
            try {
                this.grafoCarregado = algoritmos.carregarGrafo(arquivo.getAbsolutePath(), tipo);
                
                // Sucesso!
                areaResultados.setText(">>> Grafo carregado com sucesso!\n");
                areaResultados.append("Arquivo: " + arquivo.getName() + "\n");
                areaResultados.append("Representação: " + tipo.toString() + "\n");
                areaResultados.append("Vértices: " + grafoCarregado.numeroDeVertices() + "\n");
                areaResultados.append("Arestas: " + grafoCarregado.numeroDeArestas() + "\n\n");
                areaResultados.append("Pronto. Escolha um algoritmo acima.");

                // Habilita os botões e o dropdown
                dropdownAlgoritmos.setEnabled(true);
                botaoExecutar.setEnabled(true);

            } catch (Exception e) {
                // Erro (ex: Arquivo não encontrado, formato errado)
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar o grafo: " + e.getMessage(),
                    "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
                this.grafoCarregado = null;
            }
        }
    }

    /**
     * AÇÃO: Chamado quando o usuário clica no botão "Executar"
     */
    private void onExecutarAlgoritmo() {
        // Checagem de segurança
        if (this.grafoCarregado == null) {
            JOptionPane.showMessageDialog(this,
                "Você precisa carregar um grafo primeiro!",
                "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Pega o índice do item selecionado (0 = DFS, 1 = BFS, etc.)
        int indiceAlgoritmo = dropdownAlgoritmos.getSelectedIndex();

        try {
            // Roda o algoritmo com base no índice
            switch (indiceAlgoritmo) {
                case 0: // 1. Busca em Profundidade (DFS)
                    areaResultados.setText("Executando DFS (em todos os componentes)...\n");
                    algoritmos.buscaEmProfundidade(grafoCarregado);
                    areaResultados.append(formatarResultadosDFS(grafoCarregado));
                    break;
                case 1: // 2. Busca em Largura (BFS)
                    Vertice sBFS = pedirVerticeGUI("inicial (s)");
                    if (sBFS == null) return; // Usuário cancelou
                    Collection<Aresta> arestasBFS = algoritmos.buscaEmLargura(grafoCarregado, sBFS);
                    areaResultados.setText("Executando BFS a partir de " + sBFS.id() + "...\n");
                    areaResultados.append(formatarResultadosBFS(grafoCarregado, arestasBFS));
                    break;
                
                // --- CORREÇÃO DO 'SWITCH' ---
                // (O seu código estava com os 'case' 3 e 4 trocados)
                case 2: // 3. Existe Ciclo?
                    areaResultados.setText("Verificando se existe ciclo...\n");
                    boolean ciclo = algoritmos.existeCiclo(grafoCarregado);
                    areaResultados.append(">>> Resultado: O grafo " + (ciclo ? "CONTÉM" : "NÃO CONTÉM") + " ciclo.");
                    break;
                
                case 3: // 4. Componentes Fortemente Conexos (SCC)
                    areaResultados.setText("Calculando Componentes Fortemente Conexos...\n");
                    Grafo gReduzido = algoritmos.componentesFortementeConexos(grafoCarregado);
                    int[] sccMap = algoritmos.getSccMap();
                    areaResultados.append(formatarResultadosSCC(grafoCarregado, gReduzido, sccMap));
                    break;
                // --- FIM DA CORREÇÃO ---

                case 4: // 5. Árvore Geradora Mínima (AGM)
                    areaResultados.setText("Calculando Árvore Geradora Mínima (Kruskal)...\n");
                    Collection<Aresta> agm = algoritmos.arvoreGeradoraMinima(grafoCarregado);
                    double custoAGM = algoritmos.custoDaArvoreGeradora(grafoCarregado, agm);
                    areaResultados.append(">>> Resultado: Custo total da AGM = " + custoAGM + "\n");
                    areaResultados.append("Arestas da AGM: " + formatarArestas(agm));
                    break;
                case 5: // 6. Caminho Mínimo (Dijkstra)
                    areaResultados.setText("Calculando Caminho Mínimo (Dijkstra)...\n");
                    Vertice origem = pedirVerticeGUI("de ORIGEM (s)");
                    if (origem == null) return;
                    Vertice destino = pedirVerticeGUI("de DESTINO (t)");
                    if (destino == null) return;
                    
                    ArrayList<Aresta> caminho = algoritmos.caminhoMinimo(grafoCarregado, origem, destino);
                    double custoCaminho = algoritmos.custoDoCaminhoMinimo(grafoCarregado, caminho, origem, destino);
                    areaResultados.append(">>> Resultado: Custo total = " + custoCaminho + "\n");
                    areaResultados.append("Caminho de " + origem.id() + " para " + destino.id() + ": " + formatarArestas(caminho));
                    break;
                case 6: // 7. Fluxo Máximo
                    areaResultados.setText("Calculando Fluxo Máximo (Edmonds-Karp)...\n");
                    Vertice sFluxo = pedirVerticeGUI("de ORIGEM (s)");
                    if (sFluxo == null) return;
                    Vertice tFluxo = pedirVerticeGUI("de DESTINO (t)");
                    if (tFluxo == null) return;
                    
                    double fluxo = algoritmos.fluxoMaximo(grafoCarregado, sFluxo, tFluxo);
                    areaResultados.append(">>> Resultado: Fluxo Máximo de " + sFluxo.id() + " para " + tFluxo.id() + " = " + fluxo);
                    break;
            }
        } catch (Exception e) {
            // Mostra um popup de erro se algo der errado (ex: validação do custo)
            JOptionPane.showMessageDialog(this,
                "Ocorreu um erro ao executar o algoritmo: " + e.getMessage(),
                "Erro de Execução", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- MÉTODOS HELPERS (Auxiliares de formatação e entrada) ---

    /**
     * (Helper) Abre uma janela popup para pedir o ID de um vértice.
     */
    private Vertice pedirVerticeGUI(String tipo) {
        while (true) { // Loop infinito até receber uma entrada válida ou cancelar
            try {
                // Mostra um popup pedindo a entrada
                String input = JOptionPane.showInputDialog(this, "Digite o ID do vértice " + tipo + ":");
                if (input == null) {
                    return null; // Usuário clicou em "Cancelar"
                }
                int id = Integer.parseInt(input); // Tenta converter para número
                
                // Valida se o ID está dentro dos limites do grafo
                if (id < 0 || id >= grafoCarregado.numeroDeVertices()) {
                    JOptionPane.showMessageDialog(this, "Erro: Vértice com ID " + id + " não existe.", "Erro", JOptionPane.WARNING_MESSAGE);
                } else {
                    return grafoCarregado.vertices().get(id); // Sucesso!
                }
            } catch (NumberFormatException e) {
                // Erro se o usuário digitou "abc" em vez de um número
                JOptionPane.showMessageDialog(this, "Erro: Por favor, digite um número (ID).", "Erro", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }

    /**
     * (Helper) Formata uma coleção de arestas para uma impressão bonita.
     */
    private String formatarArestas(Collection<Aresta> arestas) {
        if (arestas == null || arestas.isEmpty()) {
            return "[]";
        }
        // StringBuilder é mais eficiente para construir strings grandes
        StringBuilder sb = new StringBuilder("[\n");
        for (Aresta a : arestas) {
            if (a == null) continue; // Segurança (Dijkstra pode add null se falhar)
            sb.append("  ( " + a.origem().id() + " -> " + a.destino().id() + " | Peso: " + a.peso() + " )\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * (Helper) Puxa os dados do DFS (via getters) e formata o relatório.
     */
    private String formatarResultadosDFS(Grafo g) {
        StringBuilder sb = new StringBuilder();
        sb.append("Arestas de Árvore: " + formatarArestas(algoritmos.arestasDeArvore(g)) + "\n");
        sb.append("Arestas de Retorno: " + formatarArestas(algoritmos.arestasDeRetorno(g)) + "\n");
        sb.append("Arestas de Avanço: " + formatarArestas(algoritmos.arestasDeAvanco(g)) + "\n");
        sb.append("Arestas de Cruzamento: " + formatarArestas(algoritmos.arestasDeCruzamento(g)) + "\n");

        sb.append("\n--- Tempos (d/f) ---\n");
        int[] d = algoritmos.getTempoDescobertaDFS();
        int[] f = algoritmos.getTempoFinalizacaoDFS();
        // String.format é usado para alinhar o texto em colunas
        sb.append(String.format("%-10s | %-5s | %-5s\n", "Vértice", "d[v]", "f[v]"));
        sb.append("------------------------\n");
        for (Vertice v : g.vertices()) {
            sb.append(String.format("  %-7d | %-5d | %-5d\n", v.id(), d[v.id()], f[v.id()]));
        }
        return sb.toString();
    }
    
    /**
     * (Helper) Puxa os dados do BFS (via getters) e formata o relatório.
     */
    private String formatarResultadosBFS(Grafo g, Collection<Aresta> arestas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Arestas da Árvore BFS: " + formatarArestas(arestas) + "\n");

        sb.append("\n--- Distâncias (d) e Pais (pi) ---\n");
        int[] d = algoritmos.getDistanciaBFS();
        Vertice[] pai = algoritmos.getPaiBFS();
        sb.append(String.format("%-10s | %-11s | %-11s\n", "Vértice", "d[v] (Dist)", "pi[v] (Pai)"));
        sb.append("----------------------------------------\n");
        for (Vertice v : g.vertices()) {
            String paiStr = (pai[v.id()] == null) ? "null" : String.valueOf(pai[v.id()].id());
            String distStr = (d[v.id()] == Integer.MAX_VALUE) ? "inf" : String.valueOf(d[v.id()]);
            sb.append(String.format("  %-7d | %-11s | %-11s\n", v.id(), distStr, paiStr));
        }
        return sb.toString();
    }

    /**
     * (Helper) Puxa os dados do SCC (via getters) e formata o relatório.
     */
    private String formatarResultadosSCC(Grafo gOriginal, Grafo gReduzido, int[] sccMap) {
        StringBuilder sb = new StringBuilder();
        int numSCC = gReduzido.numeroDeVertices();

        sb.append(">>> Total de Componentes encontrados: " + numSCC + "\n");

        // Classificação do Grafo
        if (numSCC == 1) {
            sb.append("\nClassificação do Grafo: Fortemente Conexo (f-conexo)\n");
        } else {
            sb.append("\nClassificação do Grafo: Não é Fortemente Conexo.\n");
        }

        // Mapeamento de Vértices para Componentes
        sb.append("\n--- Componentes ---\n");
        
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
            sb.append("Componente " + i + ": { ");
            String prefixo = "";
            for (Vertice v : componentes.get(i)) {
                sb.append(prefixo + v.id());
                prefixo = ", ";
            }
            sb.append(" }\n");
        }

        // Impressão do Grafo Reduzido
        sb.append("\n--- Grafo Reduzido (Adjacências) ---\n");
        try {
            for (Vertice u : gReduzido.vertices()) {
                sb.append(u.id() + " -> [ ");
                String prefixo = "";
                for (Vertice v : gReduzido.adjacentesDe(u)) {
                    sb.append(prefixo + v.id());
                    prefixo = ", ";
                }
                sb.append(" ]\n");
            }
        } catch (Exception e) {
            sb.append("Erro ao imprimir grafo reduzido: " + e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Ponto de entrada principal do programa (main).
     * Cria e mostra a janela da GUI.
     */
    public static void main(String[] args) {
        // Garante que o código da GUI rode na "Thread de Despacho de Eventos"
        // (É a forma correta de iniciar uma aplicação Swing)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Cria uma nova instância da nossa janela e a torna visível
                new MainFrame().setVisible(true);
            }
        });
    }

}