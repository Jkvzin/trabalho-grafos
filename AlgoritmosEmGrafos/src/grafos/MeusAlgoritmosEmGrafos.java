package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MeusAlgoritmosEmGrafos implements AlgoritmosEmGrafos {

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



    private enum Cor { BRANCO, CINZA, PRETO }
    private int[] d; // Tempo de descoberta
    private int[] f; // Tempo de finalização
    private Vertice[] pai; // Pai de cada vértice na árvore
    private Cor[] cor; // Cor (BRANCO, CINZA, PRETO) de cada vértice
    private int tempo; // "Relógio" global
    // Coleções para guardar os tipos de arestas
    private Collection<Aresta> arestasArvore;
    private Collection<Aresta> arestasRetorno;
    private Collection<Aresta> arestasAvanco;
    private Collection<Aresta> arestasCruzamento;

    @Override
    public Collection<Aresta> buscaEmProfundidade (Grafo g){
        int V = g.numeroDeVertices();

        cor = new Cor[V];
        d = new int[V];
        f = new int[V];
        pai = new Vertice[V];

        arestasArvore = new ArrayList<>();
        arestasRetorno = new ArrayList<>();
        arestasAvanco = new ArrayList<>();
        arestasCruzamento = new ArrayList<>();
        
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
            
            // Outra chamada perigosa!
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
    


    private int[] dBFS; // Distância (é o "tempo de descoberta" no BFS)
    private Vertice[] paiBFS;
    private Cor[] corBFS;
    private Collection<Aresta> arestasArvoreBFS;

    @Override
    public Collection<Aresta> buscaEmLargura (Grafo g, Vertice s){
        try{
            int V = g.numeroDeVertices();

            corBFS = new Cor[V];
            dBFS = new int[V];
            paiBFS = new Vertice[V];

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
                Vertice u = Q.remove(0);
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
    
    @Override
    public boolean existeCiclo(Grafo g){
        if (arestasRetorno == null){
            buscaEmProfundidade(g);
        }
        return !arestasRetorno.isEmpty();
    }
    


     /**
     * Identifica os componentes fortemente conexos de um grafo e retorna o grafo reduzido
     * @param g Grafo original
     * @return Grafo reduzido
     */
    public Grafo componentesFortementeConexos (Grafo g);
    



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

            int i;
            for (i = 0; i < conjuntos.size(); i++) {
                if (conjuntos.get(i).contains(v)) {
                    break;
                }
            }

            int j;
            for (j = 0; j < conjuntos.size(); j++) {
                if (conjuntos.get(j).contains(u)) {
                    break;
                }
            }

            if (i != j) {
                X.add(aresta);
                conjuntos.get(j).addAll(conjuntos.get(i));
                conjuntos.get(i).clear();
            }
        }

        return X;
        
    }

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


    public double custoDaArvoreGeradora(Grafo g, Collection<Aresta> arestas) throws Exception{
        int V = g.numeroDeVertices();
        double custo = 0;

        // Uma árvore geradora deve ter V-1 arestas.
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
    



    private Vertice[] paiC;
    private double[] dC;
    private ArrayList<Vertice> Q;
    private ArrayList<Vertice> S;
    private ArrayList<Aresta> arestasCaminhoMinimo;

    private void inicializa(Grafo g, Vertice s){
        int V = g.numeroDeVertices();
        paiC = new Vertice[V];
        dC = new double[V];
        Q = new ArrayList<>();
        S = new ArrayList<>();
        arestasCaminhoMinimo = new ArrayList<>();
        
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

    public ArrayList<Aresta> arestasCaminhoMinimoMinimo(Grafo g, Vertice origem, Vertice destino ){
        inicializa(g, origem);
        Q = g.vertices();
        while (!Q.isEmpty()) {
            Vertice u = null;
            double minDist = Double.POSITIVE_INFINITY;

            for(Vertice v : Q){
                if (dC[v.id()] < minDist) {
                    minDist = dC[v.id()];
                    u = v;
                }
            }

            if (u == null) {
                break;
            }
            Q.remove(u);
            S.add(u);
            if (u.id() == destino.id()) {
                break;
            }

            
            try {
                for (Vertice v : g.adjacentesDe(u)) {

                    ArrayList<Aresta> arestasParalelas = g.arestasEntre(u, v);
                    
                    if (arestasParalelas.isEmpty()) {
                        continue; 
                    }

                    double w = Double.POSITIVE_INFINITY;
                    for (Aresta a : arestasParalelas) {
                        if (a.peso() < w) {
                            w = a.peso();
                        }
                    }

                    relaxa(u, v, w);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
        
        // Validação final
        if (arestasCaminhoMinimo.isEmpty() && !origem.equals(destino)) return new ArrayList<>();
        if (!arestasCaminhoMinimo.isEmpty() && !arestasCaminhoMinimo.get(0).origem().equals(origem)) return new ArrayList<>();

        return arestasCaminhoMinimo;
    }
    
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
    
    /**
     * Calcula o fluxo máximo em um grafo ponderado orientado
     * @param g Grafo
     * @return o valor do fluxo máximo no grafo
     */
    public double fluxoMaximo (Grafo g, Vertice origem, Vertice destino){
        
    }
    
}