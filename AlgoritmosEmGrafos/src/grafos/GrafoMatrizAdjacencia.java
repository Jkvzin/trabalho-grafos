package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Matriz de Adjacência
 * * Nesta estrutura, o grafo é uma matriz (V x V)
 * A célula matriz[i][j] armazena o *peso* da aresta que vai do vértice 'i' para o 'j'
 * * não suporta arestas paralelas
 */
public class GrafoMatrizAdjacencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    private Double[][] matriz; 
    private int numVertices;
    private int numArestas;

    /**
     * Construtor
     * @param vertices Uma lista de todos os vértices que existirão no grafo.
     */
    public GrafoMatrizAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        this.matriz = new Double[numVertices][numVertices];
    }

    /**
     * Adiciona uma aresta com peso padrão (1.0)
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        this.adicionarAresta(origem, destino, 1.0);
    }

    /**
     * Adiciona uma aresta com peso específico ao grafo
     * Se a aresta já existir, seu peso é sobrescrito
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        int u = origem.id();
        int v = destino.id();

        if (this.matriz[u][v] == null) {
            this.numArestas++;
        }

        this.matriz[u][v] = peso; 
    }

    /**
     * Verifica se existe uma aresta entre a origem e o destino
     */
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception {
        return this.matriz[origem.id()][destino.id()] != null;
    }

    /**
     * Calcula o grau total do vértice (entrada + saida).
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        int grau = 0;
        int u = vertice.id();
        
        for (int v = 0; v < numVertices; v++) {
            if (this.matriz[u][v] != null) {
                grau++; 
            }
            if (this.matriz[v][u] != null) {
                grau++; 
            }
        }
        
        // Se existir um loop entre dois vertices vai contar duas vezes
        return grau;
    }

    /**
     * Retorna o número total de vértices
     */
    @Override
    public int numeroDeVertices() {
        return this.numVertices;
    }

    /**
     * Retorna o número total de arestas
     */
    @Override
    public int numeroDeArestas() {
        return this.numArestas;
    }

    /**
     * Retorna uma lista de vértices adjacentes 
     */
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        int u = vertice.id();
        
        for (int v = 0; v < numVertices; v++) {
            if (this.matriz[u][v] != null) {
                adjacentes.add(vertices.get(v));
            }
        }
        return adjacentes;
    }

    /**
     * Altera o peso da aresta entre a origem e o destino
     */
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        if (!this.existeAresta(origem, destino)) {
            throw new Exception("Aresta não encontrada.");
        }
        // Simplesmente sobrescreve o valor na célula
        this.matriz[origem.id()][destino.id()] = peso;
    }

    /**
     * Retorna a aresta entre origem e destino.
     * Como esta implementação não suporta arestas paralelas, ela retorna uma lista com no máximo 1 aresta.
     */
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        Double peso = this.matriz[origem.id()][destino.id()];

        if (peso != null) {
            arestasEncontradas.add(new Aresta(origem, destino, peso));
        }
        return arestasEncontradas;
    }

    /**
     * Retorna a lista de vertices
     */
    @Override
    public ArrayList<Vertice> vertices() {
        return vertices;
    }

    /**
     * Cria e retorna o grafo transposto G^T
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        Grafo gT = new GrafoMatrizAdjacencia(this.vertices);

        for (int i = 0; i < this.numVertices; i++) {
            for (int j = 0; j < this.numVertices; j++) {
                Double peso = this.matriz[i][j];
                
                if (peso != null) {
                    gT.adicionarAresta(this.vertices.get(j), this.vertices.get(i), peso);
                }
            }
        }
        return gT;
    }
}