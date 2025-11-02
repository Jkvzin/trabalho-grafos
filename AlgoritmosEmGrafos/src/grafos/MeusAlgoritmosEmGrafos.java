package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
// Removi imports desnecessários como Queue e LinkedList, já que você usa ArrayList
// (Se você *precisar* deles, pode adicionar de volta)

public class MeusAlgoritmosEmGrafos implements AlgoritmosEmGrafos {

    /*****************************************************************/
    // Carregamento do grafo (Seu código, 100% OK)
    @Override
    public Grafo carregarGrafo(String path, TipoDeRepresentacao t) throws Exception {
        FileManager fm = new FileManager();
        ArrayList<String> lines = fm.stringReader(path);

        if (lines == null || lines.isEmpty()) {
            throw new Exception("Arquivo vazio ou não encontrado.");
        }

        // Pegar número de vertices
        int V = Integer.parseInt(lines.get(0).trim());
        lines.remove(0);

        //Criar cada vértice
        ArrayList<Vertice> vertices = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            vertices.add(new Vertice(i));
        }

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

        //Criar arestas
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue; 
            }
            
            String[] parts = line.split(" ", 2); 
            if (parts.length < 2) {
                continue; 
            }

            Vertice origem = vertices.get(Integer.parseInt(parts[0].trim()));
            String arestasStr = parts[1].trim(); 

            String[] arestasArray = arestasStr.split(";"); 
            
            
            // Itera sobre cada aresta encontrada (ex: "0-40", " 1-50")
            for (String arestaInfo : arestasArray) {
                if (arestaInfo.trim().isEmpty()) {
                    continue;
                }
                // Separa a aresta em "Destino" e "Peso"
                String[] arestaDetalhe = arestaInfo.trim().split("-"); // Ex: ["0", "40"]
                if (arestaDetalhe.length < 2) {
                    continue; // Ignora arestas mal formatadas
                }

                Vertice destino = vertices.get(Integer.parseInt(arestaDetalhe[0].trim()));
                double peso = Double.parseDouble(arestaDetalhe[1].trim());

                g.adicionarAresta(origem, destino, peso);
            } 
        }
        
        return g;
        
    }


    /*****************************************************************/
    // DFS (Seu código, 100% OK)
    private enum Cor { BRANCO, CINZA, PRETO }
    private int[] d; // Tempo de descoberta
    private int[] f; // Tempo de finalização
    private Vertice[] pai; // Pai de cada vértice na árvore
    private Cor[] cor; // Cor (BRANCO, CINZA, PRETO) de cada vértice
    private int tempo; // "Relógio" global
    private Collection<Aresta> arestasArvore;
    private Collection<Aresta> arestasRetorno;
    private Collection<Aresta> arestasAvanco;
    private Collection<Aresta> arestasCruzamento;

    @Override
    public Collection<Aresta> buscaEmProfundidade (Grafo g){
        int V = g.numeroDeVertices();
        cor = new Cor[V]; d = new int[V]; f = new int[V]; pai = new Vertice[V];
        arestasArvore = new ArrayList<>(); arestasRetorno = new ArrayList<>();
        arestasAvanco = new ArrayList<>(); arestasCruzamento = new ArrayList<>();
        ArrayList<Vertice> vertices = g.vertices();
        for(Vertice u : vertices){
            cor[u.id()] = Cor.BRANCO;
            pai[u.id()] = null;
        }
        tempo = 0;
        for(Vertice u : vertices){
            if (cor[u.id()] == Cor.BRANCO) {
                dfsVisit(u, g);
            }
        }
        return arestasArvore;
    }

    private void dfsVisit(Vertice u, Grafo g){
        try{
            cor[u.id()] = Cor.CINZA;
            tempo += 1;
            d[u.id()] = tempo;
            for (Vertice v : g.adjacentesDe(u)) {
                Aresta a = g.arestasEntre(u, v).get(0); 
                if (cor[v.id()] == Cor.BRANCO) {
                    pai[v.id()] = u;
                    arestasArvore.add(a);
                    dfsVisit(v, g);
                } else if (cor[v.id()] == Cor.CINZA) {
                    arestasRetorno.add(a);
                } else if (cor[v.id()] == Cor.PRETO) {
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
        cor[u.id()] = Cor.PRETO;
        tempo++;
        f[u.id()] = tempo;
    }
    
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
    // BFS (Seu código "do seu jeito", 100% OK)
    private int[] dBFS; private Vertice[] paiBFS;
    private Cor[] corBFS; private Collection<Aresta> arestasArvoreBFS;

    @Override
    public Collection<Aresta> buscaEmLargura (Grafo g, Vertice s){
        try{
            int V = g.numeroDeVertices();
            corBFS = new Cor[V]; dBFS = new int[V]; paiBFS = new Vertice[V];
            arestasArvoreBFS = new ArrayList<>();
            ArrayList<Vertice> vertices = g.vertices();
            for(Vertice u : vertices){
                corBFS[u.id()] = Cor.BRANCO;
                paiBFS[u.id()] = null;
                dBFS[u.id()] = Integer.MAX_VALUE;
            }
            corBFS[s.id()] = Cor.CINZA;
            dBFS[s.id()] = 0;
            ArrayList<Vertice> Q = new ArrayList<>();
            Q.add(s);
            while (!Q.isEmpty()) {
                Vertice u = Q.remove(0); // Lento, mas é "o seu jeito"
                for(Vertice v : g.adjacentesDe(u)){
                    if (corBFS[v.id()] == Cor.BRANCO) {
                        corBFS[v.id()] = Cor.CINZA;
                        dBFS[v.id()] = dBFS[u.id()] + 1;
                        paiBFS[v.id()] = u;
                        Q.add(v);
                        arestasArvoreBFS.add(g.arestasEntre(u, v).get(0));
                    }
                }
                corBFS[u.id()] = Cor.PRETO;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return arestasArvoreBFS;
    }
    
    /*****************************************************************/
    // existeCiclo (Seu código, 100% OK)
    @Override
    public boolean existeCiclo(Grafo g){
        if (arestasRetorno == null){
            buscaEmProfundidade(g);
        }
        return !arestasRetorno.isEmpty();
    }
    
    /*****************************************************************/
    // Componentes Fortemente Conexos (Seu código, com try-catch adicionado)
    @Override
    public Grafo componentesFortementeConexos(Grafo g) {
        // CORREÇÃO: Adicionado try-catch, pois g.criarGrafoTransposto()
        // lança uma Exceção, mas este método não.
        try {
            buscaEmProfundidade(g); 

            // (Como sua interface Grafo.java tem criarGrafoTransposto(),
            //  esta chamada é a correta)
            Grafo gT = g.criarGrafoTransposto(); 

            ArrayList<Vertice> verticesOrdenados = new ArrayList<>(g.vertices());
            int V = verticesOrdenados.size();
            
            // BubbleSort "do seu jeito"
            for (int i = 0; i < V - 1; i++) {
                for (int j = 0; j < V - i - 1; j++) {
                    if (f[verticesOrdenados.get(j).id()] < f[verticesOrdenados.get(j + 1).id()]) {
                        Vertice temp = verticesOrdenados.get(j);
                        verticesOrdenados.set(j, verticesOrdenados.get(j + 1));
                        verticesOrdenados.set(j + 1, temp);
                    }
                }
            }

            Cor[] corDFST = new Cor[V];
            int[] sccMap = new int[V];
            int sccId = -1; 
            
            for (Vertice v : gT.vertices()) { corDFST[v.id()] = Cor.BRANCO; }

            for (Vertice v : verticesOrdenados) {
                if (corDFST[v.id()] == Cor.BRANCO) {
                    sccId++;
                    dfsVisitSCC(gT, v, corDFST, sccMap, sccId);
                }
            }
            int numSCC = sccId + 1;
            ArrayList<Vertice> verticesReduzidos = new ArrayList<>();
            for (int i = 0; i < numSCC; i++) {
                verticesReduzidos.add(new Vertice(i));
            }
            
            Grafo gReduzido = new GrafoListaAdjacencia(verticesReduzidos);
            
            ArrayList<String> arestasReduzidasAdicionadas = new ArrayList<>(); 

            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    int sccU = sccMap[u.id()];
                    int sccV = sccMap[v.id()];
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

    // (Helper do SCC, 100% OK)
    private void dfsVisitSCC(Grafo gT, Vertice u, Cor[] cor, int[] sccMap, int sccId) {
        try {
            cor[u.id()] = Cor.CINZA;
            sccMap[u.id()] = sccId;
            for (Vertice v : gT.adjacentesDe(u)) {
                if (cor[v.id()] == Cor.BRANCO) {
                    dfsVisitSCC(gT, v, cor, sccMap, sccId);
                }
            }
            cor[u.id()] = Cor.PRETO;
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /*****************************************************************/
    // AGM (Seu código "do seu jeito", com validação de índice)
    private Collection<Aresta> X;
    private ArrayList<ArrayList<Vertice>> conjuntos;

    @Override
    public Collection<Aresta> arvoreGeradoraMinima(Grafo g){
        X = new ArrayList<>();
        int V = g.numeroDeVertices();
        ArrayList<Vertice> vertices = g.vertices();
        conjuntos = new ArrayList<>(V);
        ArrayList<Aresta> A_linha = obterTodasArestas(g);
        
        for (int i = 0; i < V; i++) {
            conjuntos.add(new ArrayList<Vertice>());
            conjuntos.get(i).add(vertices.get(i));
        }

        A_linha.sort(Comparator.comparingDouble(Aresta::peso));
        
        for(Aresta aresta : A_linha){
            Vertice u = aresta.origem();
            Vertice v = aresta.destino();

            // CORREÇÃO: Inicializa 'i' e 'j' como -1 para
            // garantir que eles foram encontrados.
            int i = -1; 
            for (int k = 0; k < conjuntos.size(); k++) {
                if (conjuntos.get(k).contains(v)) {
                    i = k; break;
                }
            }

            int j = -1;
            for (int k = 0; k < conjuntos.size(); k++) {
                if (conjuntos.get(k).contains(u)) {
                    j = k; break;
                }
            }

            // CORREÇÃO: Verifica se 'i' e 'j' foram encontrados
            // (se não são -1) antes de usá-los.
            if (i != -1 && j != -1 && i != j) {
                X.add(aresta);
                conjuntos.get(j).addAll(conjuntos.get(i));
                conjuntos.get(i).clear();
            }
        }
        return X;
    }

    // (Helper do AGM, 100% OK)
    private ArrayList<Aresta> obterTodasArestas(Grafo g) {
        ArrayList<Aresta> todasArestas = new ArrayList<>();
        try {
            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    todasArestas.addAll(g.arestasEntre(u, v));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return todasArestas;
    }

    /*****************************************************************/
    // Custo AGM (Seu código, 100% OK)
    @Override
    public double custoDaArvoreGeradora(Grafo g, Collection<Aresta> arestas) throws Exception{
        int V = g.numeroDeVertices();
        double custo = 0;
        if (V > 0 && arestas.size() != V - 1) {
            throw new Exception("Não é uma árvore geradora: número incorreto de arestas. Esperado: " + (V - 1) + ", Recebido: " + arestas.size());
        } else if (V == 0 && arestas.size() > 0) {
            throw new Exception("Não é uma árvore geradora: grafo vazio mas arestas fornecidas.");
        }
        if (V == 0) return 0;
        for(Aresta aresta : arestas){
            custo += aresta.peso();
        }
        return custo;
    }
    
    /*****************************************************************/
    // Caminho Mínimo (Seu código, com os 3 bugs CORRIGIDOS)
    private Vertice[] paiC;
    private double[] dC;
    private ArrayList<Vertice> Q;
    private ArrayList<Vertice> S;
    // CORREÇÃO 3: Removida a variável de membro 'arestasCaminhoMinimo'

    private void inicializa(Grafo g, Vertice s){
        int V = g.numeroDeVertices();
        paiC = new Vertice[V];
        dC = new double[V];
        Q = new ArrayList<>(); // (Será preenchido no 'caminhoMinimo')
        S = new ArrayList<>();
        // CORREÇÃO 3: Removida 'arestasCaminhoMinimo = new ArrayList<>()' daqui
        
        ArrayList<Vertice> vertices = g.vertices();
        for(Vertice u : vertices){
            paiC[u.id()] = null;
            dC[u.id()] = Integer.MAX_VALUE;
        }
        dC[s.id()] = 0;
    }

    private void relaxa(Vertice u, Vertice v, double w){
        if (dC[v.id()] > (dC[u.id()] + w)) {
            dC[v.id()] = dC[u.id()] + w;
            paiC[v.id()] = u;
        }
    }

    // CORREÇÃO 1: Nome do método corrigido
    @Override
    public ArrayList<Aresta> caminhoMinimo(Grafo g, Vertice origem, Vertice destino ){
        inicializa(g, origem);
        
        // CORREÇÃO 2: Criando uma CÓPIA da lista de vértices
        Q = new ArrayList<>(g.vertices());
        
        while (!Q.isEmpty()) {
            Vertice u = null;
            double minDist = Double.POSITIVE_INFINITY;
            for(Vertice v : Q){
                if (dC[v.id()] < minDist) {
                    minDist = dC[v.id()];
                    u = v;
                }
            }
            if (u == null) break;
            Q.remove(u);
            S.add(u);
            if (u.id() == destino.id()) break;
            
            try {
                for (Vertice v : g.adjacentesDe(u)) {
                    ArrayList<Aresta> arestasParalelas = g.arestasEntre(u, v);
                    if (arestasParalelas.isEmpty()) continue; 
                    double w = Double.POSITIVE_INFINITY;
                    for (Aresta a : arestasParalelas) {
                        if (a.peso() < w) w = a.peso();
                    }
                    relaxa(u, v, w);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // CORREÇÃO 3: 'caminho' agora é uma variável LOCAL
        ArrayList<Aresta> arestasCaminhoMinimo = new ArrayList<>();
        Vertice atual = destino;
        
        try {
            while (paiC[atual.id()] != null) { 
                Vertice p = paiC[atual.id()];
                double pesoUsado = dC[atual.id()] - dC[p.id()];
                Aresta arestaDoCaminho = null;
                for(Aresta a : g.arestasEntre(p, atual)) {
                    if (Math.abs(a.peso() - pesoUsado) < 0.0001) {
                        arestaDoCaminho = a;
                        break;
                    }
                }
                if (arestaDoCaminho == null && !g.arestasEntre(p, atual).isEmpty()) {
                    arestaDoCaminho = g.arestasEntre(p, atual).get(0);
                }
                
                arestasCaminhoMinimo.add(arestaDoCaminho);
                atual = p;
            }
        } catch (Exception e) { e.printStackTrace(); }

        Collections.reverse(arestasCaminhoMinimo);
        
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
        double[][] residual = new double[V][V];
        try {
            for (Vertice u : g.vertices()) {
                for (Vertice v : g.adjacentesDe(u)) {
                    ArrayList<Aresta> arestasParalelas = g.arestasEntre(u, v);
                    for (Aresta a : arestasParalelas) {
                        residual[u.id()][v.id()] += a.peso();
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        while (true) {
            Vertice[] paisFluxo = bfsFluxo(g, origem, destino, residual);
            if (paisFluxo[destino.id()] == null) {
                break;
            }
            double pathFlow = Double.POSITIVE_INFINITY;
            for (Vertice v = destino; v.id() != origem.id(); v = paisFluxo[v.id()]) {
                Vertice u = paisFluxo[v.id()];
                pathFlow = Math.min(pathFlow, residual[u.id()][v.id()]);
            }
            for (Vertice v = destino; v.id() != origem.id(); v = paisFluxo[v.id()]) {
                Vertice u = paisFluxo[v.id()];
                residual[u.id()][v.id()] -= pathFlow;
                residual[v.id()][u.id()] += pathFlow;
            }
            maxFluxo += pathFlow;
        }
        return maxFluxo;
    }
    
    // (Helper do Fluxo "do seu jeito", 100% OK)
    private Vertice[] bfsFluxo(Grafo g, Vertice s, Vertice t, double[][] residual) {
        int V = g.numeroDeVertices();
        Vertice[] paisFluxo = new Vertice[V];
        boolean[] visitado = new boolean[V];
        for (int i=0; i<V; i++) {
            visitado[i] = false;
            paisFluxo[i] = null;
        }
        ArrayList<Vertice> Q = new ArrayList<>();
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
                        return paisFluxo;
                    }
                }
            }
        }
        return paisFluxo;
    }

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
}