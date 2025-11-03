package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Matriz de Adjacência.
 * * Nesta estrutura, o grafo é um array 2D (V x V).
 * A célula matriz[i][j] armazena o *peso* da aresta que vai do vértice 'i' para o 'j'.
 * * - Vantagem: Verificar se uma aresta (i, j) existe é muito rápido (O(1)).
 * - Desvantagem: Gasta muita memória (O(V^2)) e NÃO suporta arestas paralelas.
 */
public class GrafoMatrizAdjacencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    // A matriz V x V. Usamos 'Double' (com D maiúsculo) para poder
    // usar 'null' para representar a ausência de uma aresta.
    private Double[][] matriz; 
    private int numVertices;
    private int numArestas;

    /**
     * Construtor da classe.
     * @param vertices Uma lista de todos os vértices que existirão no grafo.
     */
    public GrafoMatrizAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        // Inicializa a matriz V x V. Por padrão, todas as células são 'null'.
        this.matriz = new Double[numVertices][numVertices];
    }

    /**
     * Adiciona uma aresta com peso padrão (1.0).
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        this.adicionarAresta(origem, destino, 1.0);
    }

    /**
     * Adiciona uma aresta com peso específico ao grafo.
     * Se a aresta já existir, seu peso é sobrescrito.
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        int u = origem.id();
        int v = destino.id();

        // Se a célula era 'null', significa que é uma aresta nova
        if (this.matriz[u][v] == null) {
            this.numArestas++;
        }
        // Armazena o peso diretamente na matriz
        this.matriz[u][v] = peso; 
    }

    /**
     * Verifica se existe uma aresta entre a origem e o destino.
     * Esta é a operação mais rápida (O(1)) desta implementação.
     */
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception {
        // Se a célula não for 'null', a aresta existe.
        return this.matriz[origem.id()][destino.id()] != null;
    }

    /**
     * Calcula o grau total do vértice (Grau de Entrada + Grau de Saída).
     * (Como você definiu).
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        int grau = 0;
        int u = vertice.id(); // ID do vértice que queremos checar
        
        for (int v = 0; v < numVertices; v++) {
            // 1. Checa a LINHA 'u' (Arestas de SAÍDA)
            // (Verifica se existe aresta de 'u' para 'v')
            if (this.matriz[u][v] != null) {
                grau++; 
            }
            
            // 2. Checa a COLUNA 'u' (Arestas de ENTRADA)
            // (Verifica se existe aresta de 'v' para 'u')
            if (this.matriz[v][u] != null) {
                grau++; 
            }
        }
        
        // Cuidado: Se houver um loop (u, u), ele será contado duas vezes
        // (uma como saída, uma como entrada), o que é o comportamento
        // padrão para grau em grafos não-orientados.
        return grau;
    }

    /**
     * Retorna o número total de vértices (cardinalidade de V).
     */
    @Override
    public int numeroDeVertices() {
        return this.numVertices;
    }

    /**
     * Retorna o número total de arestas (cardinalidade de E).
     */
    @Override
    public int numeroDeArestas() {
        return this.numArestas;
    }

    /**
     * Retorna uma lista de vértices adjacentes (vizinhos de SAÍDA).
     * Operação O(V) - precisa varrer a linha inteira da matriz.
     */
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        int u = vertice.id();
        
        // Itera por todas as colunas 'v' na linha 'u'
        for (int v = 0; v < numVertices; v++) {
            // Se a aresta (u, v) existe...
            if (this.matriz[u][v] != null) {
                // ...adiciona o vértice 'v' à lista.
                adjacentes.add(vertices.get(v));
            }
        }
        return adjacentes;
    }

    /**
     * Altera o peso da aresta entre a origem e o destino.
     * Operação O(1).
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
     * Como esta implementação não suporta arestas paralelas,
     * ela retorna uma lista com no máximo 1 aresta.
     * Operação O(1).
     */
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        // Pega o peso direto da matriz
        Double peso = this.matriz[origem.id()][destino.id()];

        if (peso != null) {
            // Cria o objeto Aresta "sob demanda"
            arestasEncontradas.add(new Aresta(origem, destino, peso));
        }
        return arestasEncontradas;
    }

    /**
     * Retorna a lista original de objetos Vertice.
     */
    @Override
    public ArrayList<Vertice> vertices() {
        return vertices;
    }

    /**
     * Cria e retorna um NOVO grafo que é o transposto (G^T) deste grafo.
     * (Todas as arestas (i,j) se tornam (j,i)).
     * Complexidade: O(V^2)
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        // 1. Cria um novo grafo vazio
        Grafo gT = new GrafoMatrizAdjacencia(this.vertices);

        // 2. Itera pela matriz inteira
        for (int i = 0; i < this.numVertices; i++) {
            for (int j = 0; j < this.numVertices; j++) {
                
                Double peso = this.matriz[i][j]; // Pega o peso de (i, j)
                
                // 3. Se existe uma aresta (i, j) no original...
                if (peso != null) {
                    // ...adiciona a aresta (j, i) no transposto
                    gT.adicionarAresta(this.vertices.get(j), this.vertices.get(i), peso);
                }
            }
        }
        return gT;
    }
}