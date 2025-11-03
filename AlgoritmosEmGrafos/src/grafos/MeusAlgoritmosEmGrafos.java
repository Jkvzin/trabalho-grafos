package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
// (Imports de Queue e LinkedList não são necessários
//  já que você está usando ArrayList "do seu jeito")

/**
 * Implementação principal da interface AlgoritmosEmGrafos.
 * Esta classe contém a lógica para todos os algoritmos do trabalho,
 * operando sobre qualquer grafo que siga a interface 'Grafo'.
 */
public class MeusAlgoritmosEmGrafos implements AlgoritmosEmGrafos {

    /*****************************************************************/
    // Carregamento do grafo
    /*****************************************************************/

    /**
     * Carrega um grafo a partir de um arquivo de texto.
     * @param path O caminho para o arquivo .txt.
     * @param t O TipoDeRepresentacao (Lista, Matriz Adj, Matriz Inc)
     * @return Um objeto Grafo preenchido.
     * @throws Exception Se o arquivo não for encontrado ou estiver mal formatado.
     */
    @Override
    public Grafo carregarGrafo(String path, TipoDeRepresentacao t) throws Exception {
        // Usa o FileManager para ler o arquivo
        FileManager fm = new FileManager();
        ArrayList<String> lines = fm.stringReader(path);

        if (lines == null || lines.isEmpty()) {
            throw new Exception("Arquivo vazio ou não encontrado.");
        }

        // Lê a primeira linha (número de vértices)
        int V = Integer.parseInt(lines.get(0).trim());
        lines.remove(0); // Remove a linha lida

        //Cria os Vértices (com IDs 0 a V-1)
        ArrayList<Vertice> vertices = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            vertices.add(new Vertice(i));
        }

        // Instancia a implementação de Grafo correta
        Grafo g;
        switch (t) {
            case MATRIZ_DE_ADJACENCIA:
                g = new GrafoMatrizAdjacencia(vertices);
                break;
            case LISTA_DE_ADJACENCIA:
                g = new GrafoListaAdjacencia(vertices);
                break;
            case MATRIZ_DE_INCIDENCIA:
                g = new GrafoMatrizIncidencia(vertices);
                break;
            default:
                throw new Exception("Tipo de representação desconhecido.");
        }

        // Itera pelo resto das linhas para criar as arestas
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue; // Pula linhas em branco
            }
            
            // Formato: "0 0-40; 1-50;"
            String[] parts = line.split(" ", 2); 
            if (parts.length < 2) {
                continue; 
            }

            Vertice origem = vertices.get(Integer.parseInt(parts[0].trim()));
            String arestasStr = parts[1].trim(); // "0-40; 1-50;"

            // Separa as arestas
            String[] arestasArray = arestasStr.split(";"); 
            
            for (String arestaInfo : arestasArray) {
                if (arestaInfo.trim().isEmpty()) {
                    continue;
                }
                // Formato: "0-40"
                String[] arestaDetalhe = arestaInfo.trim().split("-"); 
                if (arestaDetalhe.length < 2) {
                    continue;
                }

                Vertice destino = vertices.get(Integer.parseInt(arestaDetalhe[0].trim()));
                double peso = Double.parseDouble(arestaDetalhe[1].trim());

                g.adicionarAresta(origem, destino, peso);
            } 
        }
        
        return g;
        
    }

    /*****************************************************************/
    // Seção: Busca em Profundidade (DFS)
    /*****************************************************************/

    // Enum para as cores da busca (compartilhado com BFS)
    private enum Cor { BRANCO, CINZA, PRETO }
    
    // Variáveis de membro para guardar os resultados do DFS
    private int[] d; // Tempo de descoberta
    private int[] f; // Tempo de finalização
    private Vertice[] pai; // Pai de cada vértice na árvore
    private Cor[] cor; // Cor (BRANCO, CINZA, PRETO) de cada vértice
    private int tempo; // "Relógio" global
    
    // Coleções para guardar os tipos de arestas classificados
    private Collection<Aresta> arestasArvore;
    private Collection<Aresta> arestasRetorno;
    private Collection<Aresta> arestasAvanco;
    private Collection<Aresta> arestasCruzamento;

    /**
     * Método principal do DFS. Inicializa as variáveis e chama o 'dfsVisit'
     * para cada componente não visitado.
     */
    @Override
    public Collection<Aresta> buscaEmProfundidade (Grafo g){
        int V = g.numeroDeVertices();

        // Inicializa todos os arrays de controle
        cor = new Cor[V];
        d = new int[V];
        f = new int[V];
        pai = new Vertice[V];

        // Inicializa as listas de resultados
        arestasArvore = new ArrayList<>();
        arestasRetorno = new ArrayList<>();
        arestasAvanco = new ArrayList<>();
        arestasCruzamento = new ArrayList<>();
        
        ArrayList<Vertice> vertices = g.vertices();

        // Linha 1-2 (Pseudocódigo): Pinta todos de BRANCO
        for(Vertice u : vertices){
            cor[u.id()] = Cor.BRANCO;
            pai[u.id()] = null;
        }
        // Linha 3: Zera o relógio
        tempo = 0;

        // Linha 4-6: Chama o 'dfsVisit' para cada raiz de árvore
        for(Vertice u : vertices){
            if (cor[u.id()] == Cor.BRANCO) {
                dfsVisit(u, g);
            }
        }

        return arestasArvore; // Retorna as arestas de árvore, como pedido
    }

    /**
     * Método auxiliar recursivo do DFS (DFS-VISIT do pseudocódigo).
     */
    private void dfsVisit(Vertice u, Grafo g){
        try{
            // Linha 1-3 (Visit): Marca 'u' como CINZA e estampa o tempo 'd'
            cor[u.id()] = Cor.CINZA;
            tempo += 1;
            d[u.id()] = tempo;

            // Linha 4: para cada vizinho 'v'
            for (Vertice v : g.adjacentesDe(u)) {
                
                // Pega a primeira aresta (ignorando paralelas, "do seu jeito")
                Aresta a = g.arestasEntre(u, v).get(0); 

                // Linha 5: se 'v' é BRANCO...
                if (cor[v.id()] == Cor.BRANCO) {
                    // (É uma Aresta de Árvore)
                    pai[v.id()] = u;
                    arestasArvore.add(a);
                    dfsVisit(v, g); // Linha 6: recursão
                
                // (Lógica extra para classificação)
                } else if (cor[v.id()] == Cor.CINZA) {
                    // (É uma Aresta de Retorno - achou um ciclo)
                    arestasRetorno.add(a);
                } else if (cor[v.id()] == Cor.PRETO) {
                    // (É Aresta de Avanço or Cruzamento)
                    if (d[u.id()] < d[v.id()]) {
                        arestasAvanco.add(a);
                    } else {
                        arestasCruzamento.add(a);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Linha 7-8: Marca 'u' como PRETO e estampa o tempo 'f'
        cor[u.id()] = Cor.PRETO;
        tempo++;
        f[u.id()] = tempo;
    }
    
    // --- Getters "Lazy-Loading" para os resultados do DFS ---

    @Override
    public Collection<Aresta> arestasDeArvore(Grafo g){
        if (arestasArvore == null){
            buscaEmProfundidade(g);
        }
        return arestasArvore;
    }
    
    @Override
    public Collection<Aresta> arestasDeRetorno(Grafo g){
        if (arestasRetorno == null){
            buscaEmProfundidade(g);
        }
        return arestasRetorno;
    }
    
    @Override
    public Collection<Aresta> arestasDeAvanco(Grafo g){
        if (arestasAvanco == null){
            buscaEmProfundidade(g);
        }
        return arestasAvanco;
    }
    
    @Override
    public Collection<Aresta> arestasDeCruzamento(Grafo g){
        if (arestasCruzamento == null){
            buscaEmProfundidade(g);
        }
        return arestasCruzamento;
    }
    
    /*****************************************************************/
    // Seção: Busca em Largura (BFS)
    /*****************************************************************/
    
    // Variáveis de membro para os resultados do BFS
    private int[] dBFS; // Distância (d[v])
    private Vertice[] paiBFS; // Pai (pi[v])
    private Cor[] corBFS;
    private Collection<Aresta> arestasArvoreBFS; // Resultado (arestas da árvore)

    @Override
    public Collection<Aresta> buscaEmLargura (Grafo g, Vertice s){
        try{
            int V = g.numeroDeVertices();

            // Inicializa os arrays de controle
            corBFS = new Cor[V];
            dBFS = new int[V];
            paiBFS = new Vertice[V];
            arestasArvoreBFS = new ArrayList<>();
            
            ArrayList<Vertice> vertices = g.vertices();

            // Linha 1-4 (Pseudocódigo): Inicializa todos
            for(Vertice u : vertices){
                corBFS[u.id()] = Cor.BRANCO;
                paiBFS[u.id()] = null;
                dBFS[u.id()] = Integer.MAX_VALUE; // "Infinito"
            }
            
            // Linha 5-7: Inicializa a origem 's'
            corBFS[s.id()] = Cor.CINZA;
            dBFS[s.id()] = 0;

            // Linha 8: Cria a Fila (do "seu jeito", com ArrayList)
            ArrayList<Vertice> Q = new ArrayList<>();
            
            // Linha 9: Enfileira 's'
            Q.add(s);

            // Linha 10: Loop principal
            while (!Q.isEmpty()) {
                // Linha 11: Desenfileira 'u'
                Vertice u = Q.remove(0); // Lento (O(V)), mas é "o seu jeito"
                
                // Linha 12: Itera nos vizinhos de 'u'
                for(Vertice v : g.adjacentesDe(u)){
                    // Linha 13: Se 'v' é BRANCO...
                    if (corBFS[v.id()] == Cor.BRANCO) {
                        // Linhas 14-16: Atualiza 'v'
                        corBFS[v.id()] = Cor.CINZA;
                        dBFS[v.id()] = dBFS[u.id()] + 1;
                        paiBFS[v.id()] = u;
                        // Linha 17: Enfileira 'v'
                        Q.add(v);
                        
                        // (Lógica extra para o trabalho: salvar a aresta)
                        arestasArvoreBFS.add(g.arestasEntre(u, v).get(0));
                    }
                }
                // Linha 18: Finaliza 'u'
                corBFS[u.id()] = Cor.PRETO;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return arestasArvoreBFS;
    }
    
    /*****************************************************************/
    // Seção: Algoritmos de Conectividade
    /*****************************************************************/
    
    /**
     * Verifica se existe ciclo no grafo (orientado).
     * Um ciclo existe se, e somente se, o DFS encontra uma Aresta de Retorno.
     */
    @Override
    public boolean existeCiclo(Grafo g){
        // Roda o DFS (se não foi rodado) e pega a lista de Arestas de Retorno
        if (arestasRetorno == null){
            buscaEmProfundidade(g);
        }
        // Se a lista não está vazia, há um ciclo.
        return !arestasRetorno.isEmpty();
    }
    
    // Variável de membro para o resultado do SCC (para o 'getter')
    private int[] sccMap;

    /**
     * Identifica os componentes fortemente conexos (Algoritmo de Kosaraju).
     * @param g Grafo original
     * @return Grafo reduzido
     */
    @Override
    public Grafo componentesFortementeConexos(Grafo g) {
        // CORREÇÃO: Adicionado try-catch para g.criarGrafoTransposto()
        try {
            // PASSO 1: Roda DFS(G) para preencher 'this.f'
            buscaEmProfundidade(g); 

            // PASSO 2: Cria o grafo transposto G^T
            Grafo gT = g.criarGrafoTransposto();

            // PASSO 3 (A): Ordena os vértices por 'f' decrescente (BubbleSort)
            ArrayList<Vertice> verticesOrdenados = new ArrayList<>(g.vertices());
            int V = verticesOrdenados.size();
            for (int i = 0; i < V - 1; i++) {
                for (int j = 0; j < V - i - 1; j++) {
                    if (f[verticesOrdenados.get(j).id()] < f[verticesOrdenados.get(j + 1).id()]) {
                        Vertice temp = verticesOrdenados.get(j);
                        verticesOrdenados.set(j, verticesOrdenados.get(j + 1));
                        verticesOrdenados.set(j + 1, temp);
                    }
                }
            }

            // PASSO 3 (B): Roda DFS(G^T) na ordem ordenada
            Cor[] corDFST = new Cor[V];
            this.sccMap = new int[V]; // Usa a variável de membro
            int sccId = -1; 
            for (Vertice v : gT.vertices()) { corDFST[v.id()] = Cor.BRANCO; }

            for (Vertice v : verticesOrdenados) {
                if (corDFST[v.id()] == Cor.BRANCO) {
                    sccId++;
                    dfsVisitSCC(gT, v, corDFST, sccId); // Helper modificado
                }
            }
            
            // PASSO 4: Constrói o Grafo Reduzido
            int numSCC = sccId + 1;
            ArrayList<Vertice> verticesReduzidos = new ArrayList<>();
            for (int i = 0; i < numSCC; i++) {
                verticesReduzidos.add(new Vertice(i));
            }
            
            Grafo gReduzido = new GrafoListaAdjacencia(verticesReduzidos);
            ArrayList<String> arestasReduzidasAdicionadas = new ArrayList<>(); // "do seu jeito"

            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    int sccU = this.sccMap[u.id()];
                    int sccV = this.sccMap[v.id()];
                    String arestaStr = sccU + "->" + sccV;
                    
                    if (sccU != sccV && !arestasReduzidasAdicionadas.contains(arestaStr)) {
                        gReduzido.adicionarAresta(verticesReduzidos.get(sccU), verticesReduzidos.get(sccV));
                        arestasReduzidasAdicionadas.add(arestaStr);
                    }
                }
            }
            return gReduzido;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna null em caso de erro
        }
    }
    
    /** * HELPER do SCC: DFS-VISIT modificado para o G^T
     * (Não calcula tempo, só "pinta" os vértices com o ID do SCC)
     */
    private void dfsVisitSCC(Grafo gT, Vertice u, Cor[] cor, int sccId) {
        try {
            cor[u.id()] = Cor.CINZA;
            this.sccMap[u.id()] = sccId; // Salva o ID do SCC no mapa de membro
            
            for (Vertice v : gT.adjacentesDe(u)) {
                if (cor[v.id()] == Cor.BRANCO) {
                    dfsVisitSCC(gT, v, cor, sccId);
                }
            }
            cor[u.id()] = Cor.PRETO;
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /*****************************************************************/
    // Seção: Árvore Geradora Mínima (AGM)
    /*****************************************************************/
    
    // Variáveis de membro para o Kruskal "do seu jeito"
    private Collection<Aresta> X; // O resultado (a AGM)
    private ArrayList<ArrayList<Vertice>> conjuntos; // Sua estrutura de "conjuntos"

    /**
     * Encontra a AGM usando o Algoritmo de Kruskal (com sua lógica de conjuntos).
     */
    @Override
    public Collection<Aresta> arvoreGeradoraMinima(Grafo g){
        // Linha 1: X <- {}
        X = new ArrayList<>();
        int V = g.numeroDeVertices();
        ArrayList<Vertice> vertices = g.vertices();
        conjuntos = new ArrayList<>(V);
        
        // Pega todas as arestas (do seu jeito, O(V+E^2) ou pior)
        ArrayList<Aresta> A_linha = obterTodasArestas(g);
        
        // Linhas 2-4: criarConjunto(v)
        for (int i = 0; i < V; i++) {
            conjuntos.add(new ArrayList<Vertice>());
            conjuntos.get(i).add(vertices.get(i));
        }

        // Linha 5: Ordena as arestas por peso
        A_linha.sort(Comparator.comparingDouble(Aresta::peso));
        
        // Linha 6: Para cada aresta...
        for(Aresta aresta : A_linha){
            Vertice u = aresta.origem();
            Vertice v = aresta.destino();

            // Encontra o conjunto de 'v' (índice 'i')
            int i = -1; // CORREÇÃO: Inicializa com -1
            for (int k = 0; k < conjuntos.size(); k++) {
                if (conjuntos.get(k).contains(v)) {
                    i = k; break;
                }
            }

            // Encontra o conjunto de 'u' (índice 'j')
            int j = -1; // CORREÇÃO: Inicializa com -1
            for (int k = 0; k < conjuntos.size(); k++) {
                if (conjuntos.get(k).contains(u)) {
                    j = k; break;
                }
            }

            // Linha 7: se conjuntoDe(u) != conjuntoDe(v)
            // CORREÇÃO: Valida se 'i' e 'j' foram encontrados
            if (i != -1 && j != -1 && i != j) {
                // Linha 8: Adiciona aresta à AGM
                X.add(aresta);
                // Linha 9: aplicarUniao (joga todos de 'i' em 'j' e limpa 'i')
                conjuntos.get(j).addAll(conjuntos.get(i));
                conjuntos.get(i).clear();
            }
        }
        return X;
    }

    /**
     * HELPER do Kruskal: Pega todas as arestas do grafo, incluindo paralelas.
     */
    private ArrayList<Aresta> obterTodasArestas(Grafo g) {
        ArrayList<Aresta> todasArestas = new ArrayList<>();
        try {
            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    // .addAll() garante que pegamos arestas paralelas
                    todasArestas.addAll(g.arestasEntre(u, v));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return todasArestas;
    }

    /**
     * Calcula o custo de uma dada Coleção de Arestas.
     * Valida se a coleção é uma árvore (tem V-1 arestas).
     */
    @Override
    public double custoDaArvoreGeradora(Grafo g, Collection<Aresta> arestas) throws Exception{
        int V = g.numeroDeVertices();
        double custo = 0;

        // Validação simples (como você definiu)
        if (V > 0 && arestas.size() != V - 1) {
            throw new Exception("Não é uma árvore geradora: número incorreto de arestas. Esperado: " + (V - 1) + ", Recebido: " + arestas.size());
        } else if (V == 0 && arestas.size() > 0) {
            throw new Exception("Não é uma árvore geradora: grafo vazio mas arestas fornecidas.");
        }
        if (V == 0) return 0;

        // Soma os pesos
        for(Aresta aresta : arestas){
            custo += aresta.peso();
        }

        return custo;
    }
    
    /*****************************************************************/
    // Seção: Caminho Mínimo (Dijkstra)
    /*****************************************************************/
    
    // Variáveis de membro para o Dijkstra
    private Vertice[] paiC;
    private double[] dC;
    private ArrayList<Vertice> Q; // (Q do pseudocódigo, "do seu jeito")
    private ArrayList<Vertice> S; // (S do pseudocódigo)
    // (A lista de arestas do caminho agora é local)

    /**
     * Helper do Dijkstra: INICIALIZA(G, s)
     */
    private void inicializa(Grafo g, Vertice s){
        int V = g.numeroDeVertices();
        paiC = new Vertice[V];
        dC = new double[V];
        Q = new ArrayList<>(); // (Será preenchido no 'caminhoMinimo')
        S = new ArrayList<>();
        
        ArrayList<Vertice> vertices = g.vertices();
        for(Vertice u : vertices){
            paiC[u.id()] = null;             // pi[u] <- NULL
            dC[u.id()] = Integer.MAX_VALUE;  // d[u] <- infinito
        }
        dC[s.id()] = 0; // d[s] <- 0
    }

    /**
     * Helper do Dijkstra: RELAXA(u, v, w)
     */
    private void relaxa(Vertice u, Vertice v, double w){
        // se d[v] > d[u] + w(u,v)
        if (dC[v.id()] > (dC[u.id()] + w)) {
            // d[v] <- d[u] + w(u,v)
            dC[v.id()] = dC[u.id()] + w;
            // pi[v] <- u
            paiC[v.id()] = u;
        }
    }

    /**
     * Método principal do Caminho Mínimo (Dijkstra "do seu jeito").
     * CORREÇÃO 1: Nome do método corrigido para bater com a interface.
     */
    @Override
    public ArrayList<Aresta> caminhoMinimo(Grafo g, Vertice origem, Vertice destino ){
        inicializa(g, origem);
        
        // CORREÇÃO 2: Criando uma CÓPIA da lista (Q <- V)
        Q = new ArrayList<>(g.vertices());
        
        // Linha: Enquanto Q não está vazio
        while (!Q.isEmpty()) {
            
            // Linha: u <- extrairMinimo(Q) ("do seu jeito", O(V))
            Vertice u = null;
            double minDist = Double.POSITIVE_INFINITY;
            for(Vertice v : Q){
                if (dC[v.id()] < minDist) {
                    minDist = dC[v.id()];
                    u = v;
                }
            }

            if (u == null) break; // Vértices restantes são inalcançáveis
            
            Q.remove(u); // Remove 'u' de Q
            
            // Linha: S <- S U {u}
            S.add(u);
            
            // Otimização: Se achamos o destino, podemos parar
            if (u.id() == destino.id()) {
                break;
            }

            // Linha: para cada v...
            try {
                for (Vertice v : g.adjacentesDe(u)) {
                    // (Lógica correta para tratar arestas paralelas)
                    ArrayList<Aresta> arestasParalelas = g.arestasEntre(u, v);
                    if (arestasParalelas.isEmpty()) continue; 

                    double w = Double.POSITIVE_INFINITY;
                    for (Aresta a : arestasParalelas) {
                        if (a.peso() < w) w = a.peso();
                    }
                    
                    // Linha: relaxa(u, v, w)
                    relaxa(u, v, w);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // Fim do while

        // --- Reconstrução do Caminho ---
        
        // CORREÇÃO 3: 'caminho' agora é uma variável LOCAL
        ArrayList<Aresta> arestasCaminhoMinimo = new ArrayList<>();
        Vertice atual = destino;
        
        try {
            while (paiC[atual.id()] != null) { 
                Vertice p = paiC[atual.id()];
                
                // Acha a aresta exata que o Dijkstra usou
                double pesoUsado = dC[atual.id()] - dC[p.id()];
                Aresta arestaDoCaminho = null;
                for(Aresta a : g.arestasEntre(p, atual)) {
                    if (Math.abs(a.peso() - pesoUsado) < 0.0001) {
                        arestaDoCaminho = a;
                        break;
                    }
                }
                // Plano B (caso de erro de precisão de double)
                if (arestaDoCaminho == null && !g.arestasEntre(p, atual).isEmpty()) {
                    arestaDoCaminho = g.arestasEntre(p, atual).get(0);
                }
                
                arestasCaminhoMinimo.add(arestaDoCaminho);
                atual = p;
            }
        } catch (Exception e) { e.printStackTrace(); }

        Collections.reverse(arestasCaminhoMinimo); // Inverte para (origem -> destino)
        
        // Validação final
        if (arestasCaminhoMinimo.isEmpty() && !origem.equals(destino)) return new ArrayList<>();
        if (!arestasCaminhoMinimo.isEmpty() && !arestasCaminhoMinimo.get(0).origem().equals(origem)) return new ArrayList<>();

        return arestasCaminhoMinimo;
    }
    
    /*****************************************************************/
    // Custo Caminho Mínimo (Seu código, 100% OK)
    @Override
    public double custoDoCaminhoMinimo (Grafo g, ArrayList<Aresta> arestas, Vertice origem, Vertice destino ) throws Exception{
        double custoTotal = 0;
        Vertice verticeAtual = origem;

        for (Aresta a : arestas) {
            if (a == null) continue; // Segurança
            if (a.origem().id() != verticeAtual.id()) {
                throw new Exception("A sequência de arestas não forma um caminho válido: aresta " + a.origem().id() + "->" + a.destino().id() + " esperava começar de " + verticeAtual.id());
            }
            custoTotal += a.peso();
            verticeAtual = a.destino();
        }
        
        if (verticeAtual.id() != destino.id()) {
            throw new Exception("O caminho não termina no vértice de destino esperado. Terminou em: " + verticeAtual.id() + ", Esperado: " + destino.id());
        }
        return custoTotal;
    }
    
    /*****************************************************************/
    // Fluxo Máximo (Seu código, 100% OK "do seu jeito")
    @Override
    public double fluxoMaximo(Grafo g, Vertice origem, Vertice destino) {
        int V = g.numeroDeVertices();
        double maxFluxo = 0;
        
        // 1. Cria o grafo residual (matriz de capacidade)
        double[][] residual = new double[V][V];
        try {
            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    // (Soma capacidades de arestas paralelas)
                    ArrayList<Aresta> arestasParalelas = g.arestasEntre(u, v);
                    for (Aresta a : arestasParalelas) {
                        residual[u.id()][v.id()] += a.peso();
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        // 2. Loop Ford-Fulkerson
        while (true) {
            // 3. Acha caminho no residual com BFS
            Vertice[] paisFluxo = bfsFluxo(g, origem, destino, residual);

            // 4. Se não há caminho, fim.
            if (paisFluxo[destino.id()] == null) {
                break;
            }

            // 5. Acha o gargalo (pathFlow)
            double pathFlow = Double.POSITIVE_INFINITY;
            for (Vertice v = destino; v.id() != origem.id(); v = paisFluxo[v.id()]) {
                Vertice u = paisFluxo[v.id()];
                pathFlow = Math.min(pathFlow, residual[u.id()][v.id()]);
            }

            // 6. Atualiza o residual
            for (Vertice v = destino; v.id() != origem.id(); v = paisFluxo[v.id()]) {
                Vertice u = paisFluxo[v.id()];
                residual[u.id()][v.id()] -= pathFlow; // Aresta direta
                residual[v.id()][u.id()] += pathFlow; // Aresta reversa
            }

            // 7. Soma o fluxo
            maxFluxo += pathFlow;
        }

        return maxFluxo;
    }
    
    /**
     * HELPER: BFS para o Fluxo Máximo ("do seu jeito").
     */
    private Vertice[] bfsFluxo(Grafo g, Vertice s, Vertice t, double[][] residual) {
        int V = g.numeroDeVertices();
        Vertice[] paisFluxo = new Vertice[V];
        boolean[] visitado = new boolean[V];
        for (int i=0; i<V; i++) {
            visitado[i] = false;
            paisFluxo[i] = null;
        }

        ArrayList<Vertice> Q = new ArrayList<>(); // Fila com ArrayList
        Q.add(s);
        visitado[s.id()] = true;
        
        while (!Q.isEmpty()) {
            Vertice u = Q.remove(0); // Lento, mas é "o seu jeito"
            
            for (Vertice v : g.vertices()) { 
                if (!visitado[v.id()] && residual[u.id()][v.id()] > 0) {
                    visitado[v.id()] = true;
                    paisFluxo[v.id()] = u;
                    Q.add(v);
                    
                    if (v.id() == t.id()) {
                        return paisFluxo; // Achou
                    }
                }
            }
        }
        return paisFluxo; // Não achou
    }

    // --- Getters para o Main (100% OK) ---

    /** Retorna o array de tempos de descoberta do DFS */
    public int[] getTempoDescobertaDFS() {
        return this.d;
    }

    /** Retorna o array de tempos de finalização do DFS */
    public int[] getTempoFinalizacaoDFS() {
        return this.f;
    }

    /** Retorna o array de pais do DFS */
    public Vertice[] getPaiDFS() {
        return this.pai;
    }
    
    /** Retorna o array de distâncias (d) do BFS */
    public int[] getDistanciaBFS() {
        return this.dBFS;
    }
    
    /** Retorna o array de pais (pi) do BFS */
    public Vertice[] getPaiBFS() {
        return this.paiBFS;
    }

    /** Retorna o mapa de Componentes Fortemente Conexos */
    public int[] getSccMap() {
        return this.sccMap;
    }
}