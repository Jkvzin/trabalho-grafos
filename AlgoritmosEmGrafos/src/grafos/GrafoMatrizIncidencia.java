package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Matriz de Incidência.
 * * Nesta estrutura, o grafo é representado por duas listas:
 * 1. Uma lista de Arestas (`arestas`): A "lista mestra" que armazena os objetos Aresta (com seu peso).
 * 2. Uma Matriz (V x E) (`matriz`): Armazena a topologia.
 * * A célula matriz[v][e] (linha 'v', coluna 'e') indica como a aresta 'e' se conecta ao vértice 'v':
 * -1.0 : A aresta 'e' SAI do vértice 'v' (origem).
 * +1.0 : A aresta 'e' ENTRA no vértice 'v' (destino).
 * 2.0 : A aresta 'e' é um LOOP no vértice 'v'.
 * 0.0 : A aresta 'e' não toca o vértice 'v'.
 * * - Vantagem: Suporta arestas paralelas nativamente.
 * - Desvantagem: A maioria das operações (como adjacentesDe, existeAresta) 
 * são lentas (O(E) ou O(V*E)), pois não há acesso direto.
 */
public class GrafoMatrizIncidencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    // A "lista mestra" de arestas. O índice 'e' desta lista
    // corresponde à coluna 'e' da matriz.
    private ArrayList<Aresta> arestas;
    // A matriz de incidência V x E (representada como lista de listas)
    private ArrayList<ArrayList<Double>> matriz;
    private int numVertices;
    private int numArestas; // Também é o número de colunas na matriz

    /**
     * Construtor da classe.
     * @param vertices Uma lista de todos os vértices que existirão no grafo.
     */
    public GrafoMatrizIncidencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        
        // Inicializa a lista mestra de arestas
        this.arestas = new ArrayList<>();
        // Inicializa a matriz "externa" (as V linhas)
        this.matriz = new ArrayList<>(this.numVertices);

        // Para cada linha (vértice), cria uma lista "interna" (colunas) vazia
        for (int i = 0; i < this.numVertices; i++) {
            this.matriz.add(new ArrayList<>());
        }
    }

    /**
     * Adiciona uma aresta com peso padrão (1.0).
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        // Deixa a exceção "subir" para quem chamou
        this.adicionarAresta(origem, destino, 1.0);
    }

    /**
     * Adiciona uma aresta com peso específico ao grafo.
     * Esta operação é O(V) porque precisa adicionar um item em cada linha.
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        // 1. Cria o objeto Aresta e o armazena na lista mestra
        Aresta novaAresta = new Aresta(origem, destino, peso);
        this.arestas.add(novaAresta); // O índice desta aresta será 'numArestas'

        // 2. Adiciona a nova coluna (de índice 'numArestas') na matriz
        boolean isLoop = (origem.id() == destino.id());
        for (int v_id = 0; v_id < this.numVertices; v_id++) {
            
            ArrayList<Double> linha = this.matriz.get(v_id);
            
            if (isLoop && v_id == origem.id()) {
                linha.add(2.0); // Convenção para loop
            } else if (v_id == origem.id()) {
                linha.add(-1.0); // Convenção para origem
            } else if (v_id == destino.id()) {
                linha.add(1.0); // Convenção para destino
            } else {
                linha.add(0.0); // Não incide
            }
        }
        // 3. Incrementa o contador de arestas (colunas)
        this.numArestas++;
    }

    /**
     * Verifica se existe pelo menos uma aresta entre a origem e o destino.
     * Operação lenta (O(E)), pois precisa varrer a lista mestra de arestas.
     */
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        // Itera pela lista mestra O(E)
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                return true; // Achou!
            }
        }
        // Se o loop terminar, não existe.
        return false;
    }

    /**
     * Calcula o grau total do vértice (Grau de Entrada + Grau de Saída).
     * Operação O(E) (tamanho da linha do vértice).
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        if (vertice == null) {
             throw new Exception("Vértice nulo.");
        }
        
        int grau = 0;
        // Pega a linha (O(1))
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        // Itera pelas colunas (O(E))
        // (Sua convenção: +1, -1, e 2 são todos != 0)
        for (Double i : linhaVertice) {
            if (i != 0) {
                grau++;
            }
        }
        return grau;
    }

    /**
     * Retorna o número total de vértices (cardinalidade de V).
     */
    @Override
    public int numeroDeVertices() {
        return numVertices;
    }

    /**
     * Retorna o número total de arestas (cardinalidade de E).
     */
    @Override
    public int numeroDeArestas() {
        return numArestas;
    }

    /**
     * Retorna uma lista de vértices adjacentes (vizinhos de SAÍDA).
     * Operação lenta (O(E)).
     */
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        if (vertice == null) {
             throw new Exception("Vértice nulo.");
        }
        
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        // Itera por todas as colunas 'i' (arestas)
        for (int i = 0; i < numArestas; i++) {
            // (Sua convenção: < 0 (-1) ou 2.0 (loop) são arestas de saída)
            if (linhaVertice.get(i) < 0 || linhaVertice.get(i) == 2.0) {
                // Pega a aresta 'i' da lista mestra
                Aresta aresta = arestas.get(i);
                // Adiciona o destino dela
                adjacentes.add(aresta.destino());
            }
        }
        return adjacentes;
    }

    /**
     * Altera o peso da primeira aresta encontrada entre a origem e o destino.
     * Operação lenta (O(E)).
     */
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        // Itera pela lista mestra O(E)
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                a.setarPeso(peso);
                return; // Para na primeira que achar
            }
        }
        // Se o loop terminar sem achar, lança um erro
        throw new Exception("Aresta não encontrada para setar peso.");
    }

    /**
     * Retorna uma coleção de TODAS as arestas entre origem e destino.
     * (Suporta arestas paralelas).
     * Operação lenta (O(E)).
     */
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        // Itera pela lista mestra O(E)
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                arestasEncontradas.add(a);
            }
        }
        return arestasEncontradas;
    }

    /**
     * Retorna a lista original de objetos Vertice.
     */
    @Override
    public ArrayList<Vertice> vertices() {
        return this.vertices;
    }

    /**
     * Cria e retorna um NOVO grafo que é o transposto (G^T) deste grafo.
     * (Todas as arestas (u,v) se tornam (v,u)).
     * Complexidade: O(E * V) (pois O(E) * O(V) de adicionarAresta)
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        // 1. Cria um novo grafo vazio
        Grafo gT = new GrafoMatrizIncidencia(this.vertices);
        
        // 2. Itera pela lista mestra de arestas (O(E))
        for (Aresta a : this.arestas) {
            // 3. Adiciona a aresta invertida (destino -> origem)
            // Esta operação gT.adicionarAresta() é O(V)
            gT.adicionarAresta(a.destino(), a.origem(), a.peso());
        }
        return gT;
    }
}