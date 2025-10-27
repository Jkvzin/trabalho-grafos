package grafos;

import java.util.ArrayList;
import java.util.Collection;

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
    
    /**
     * Realiza busca em largura no grafo 
     * @param g Grafo
     * @return as arestas da árvore resultante
     */
    public Collection<Aresta> buscaEmLargura (Grafo g);
    
    /**
     * Verifica se existe ciclo no grafo.
     * @param g Grafo.
     * @return True, se existe ciclo, False, em caso contrário.
     */
    public boolean existeCiclo(Grafo g);
    
     /**
     * Identifica os componentes fortemente conexos de um grafo e retorna o grafo reduzido
     * @param g Grafo original
     * @return Grafo reduzido
     */
    public Grafo componentesFortementeConexos (Grafo g);
    
    /**
     * Retorna a árvore geradora mínima.
     * @param g O grafo.
     * @return Retorna a árvore geradora mínima.
     */
    public Collection<Aresta> arvoreGeradoraMinima(Grafo g);
    
    /**
     * Calcula o custo de uma árvore geradora.
     * @param arestas As arestas que compoem a árvore geradora.
     * @param g O grafo.
     * @return O custo da árvore geradora.
     * @throws java.lang.Exception Se a árvore apresentada não é geradora do grafo.
     */
    public double custoDaArvoreGeradora(Grafo g, Collection<Aresta> arestas) throws Exception;
    
    /**
     * Retorna (em ordem) as arestas que compoem o caminho mais curto 
     * entre um par de vértices. Esta função considera o peso das arestas
     * para composição do caminho mais curto.
     * @param g O grafo
     * @param origem Vértice de origem
     * @param destino Vértice de destino
     * @return As arestas (em ordem) do caminho mais curto.
     */
    public ArrayList<Aresta> caminhoMinimo(Grafo g, Vertice origem, Vertice destino );
    
    /**
     * Dado um caminho, esta função calcula o custo do caminho.
     * @param arestas Arestas que compõem o caminho
     * @param g Grafo
     * @param origem Vértice de origem
     * @param destino Vértice de destino
     * @return o custo da caminho.
     * @throws java.lang.Exception Se a sequencia apresentada não é um caminho
     * entre origem e destino.
     */
    public double custoDoCaminhoMinimo (Grafo g, ArrayList<Aresta> arestas, Vertice origem, Vertice destino ) throws Exception;
    
    /**
     * Calcula o fluxo máximo em um grafo ponderado orientado
     * @param g Grafo
     * @return o valor do fluxo máximo no grafo
     */
    public double fluxoMaximo (Grafo g);
    
}
