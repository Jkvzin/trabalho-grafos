package grafos;

import java.util.ArrayList;

/**
 * Versão PADRÃO da Matriz de Adjacência
 * NÃO suporta arestas paralelas, mas é O(1) para a maioria das operações.
 */
public class GrafoMatrizAdjacencia implements Grafo {
    private ArrayList<Vertice> vertices;
    private Double[][] matriz; 
    private int numVertices;
    private int numArestas;

    public GrafoMatrizAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        this.matriz = new Double[numVertices][numVertices];
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        this.adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        int u = origem.id();
        int v = destino.id();

        if (this.matriz[u][v] == null) {
            this.numArestas++;
        }
        this.matriz[u][v] = peso; 
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception { // Adicionado throws
        return this.matriz[origem.id()][destino.id()] != null;
    }

    /**
     * Calcula o Grau de Entrada + Grau de Saída.
     * (Seu código estava correto!)
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        int grau = 0;
        int u = vertice.id();
        for (int v = 0; v < numVertices; v++) {
            if (this.matriz[u][v] != null) {
                grau++; // Conta Saída
            }
            if (this.matriz[v][u] != null) {
                grau++; // Conta Entrada
            }
        }
        return grau;
    }

    @Override
    public int numeroDeVertices() {
        return this.numVertices;
    }

    @Override
    public int numeroDeArestas() {
        return this.numArestas;
    }

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

    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        // A checagem de existeAresta já lança exceção se for null
        if (!this.existeAresta(origem, destino)) {
            throw new Exception("Aresta não encontrada.");
        }
        this.matriz[origem.id()][destino.id()] = peso;
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        Double peso = this.matriz[origem.id()][destino.id()];

        if (peso != null) {
            arestasEncontradas.add(new Aresta(origem, destino, peso));
        }
        return arestasEncontradas;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        return vertices;
    }

    /**
     * Cria o grafo transposto O(V^2)
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        Grafo gT = new GrafoMatrizAdjacencia(this.vertices);

        for (int i = 0; i < this.numVertices; i++) {
            for (int j = 0; j < this.numVertices; j++) {
                Double peso = this.matriz[i][j];
                if (peso != null) {
                    // Adiciona a aresta (j, i) no novo grafo
                    gT.adicionarAresta(this.vertices.get(j), this.vertices.get(i), peso);
                }
            }
        }
        return gT;
    }
}