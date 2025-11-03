package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Matriz de Incidência.
 * * Nesta estrutura, o grafo é representado por duas listas:
 * 1. Uma lista de Arestas (`arestas`): A "lista mestra" que armazena os objetos Aresta (com seu peso).
 * 2. Uma Matriz (V x A) (`matriz`): Armazena a topologia.
 * * A célula matriz[v][e] (linha 'v', coluna 'a') indica como a aresta 'a' se conecta ao vértice 'v':
 * -1.0 : A aresta 'a' SAI do vértice 'v' (origem).
 * +1.0 : A aresta 'a' ENTRA no vértice 'v' (destino).
 * 2.0 : A aresta 'a' é um LOOP no vértice 'v'.
 * 0.0 : A aresta 'a' não toca o vértice 'v'.
 */
public class GrafoMatrizIncidencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    // A lista de arestas. O índice 'i' desta lista corresponde à coluna 'i' da matriz.
    private ArrayList<Aresta> arestas;
    // A matriz de incidência V x A (representada como lista de listas)
    private ArrayList<ArrayList<Double>> matriz;
    private int numVertices;
    private int numArestas;

    /**
     * Construtor
     * @param vertices Uma lista de todos os vértices que existirão no grafo
     */
    public GrafoMatrizIncidencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;   
        this.arestas = new ArrayList<>();
        this.matriz = new ArrayList<>(this.numVertices);
        for (int i = 0; i < this.numVertices; i++) {
            this.matriz.add(new ArrayList<>());
        }
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
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        Aresta novaAresta = new Aresta(origem, destino, peso);
        this.arestas.add(novaAresta);

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
        this.numArestas++;
    }

    /**
     * Verifica se existe pelo menos uma aresta entre a origem e o destino
     */
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula o grau total do vértice (entrada + saida)
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        if (vertice == null) {
             throw new Exception("Vértice nulo.");
        }
        
        int grau = 0;
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());
        for (Double i : linhaVertice) {
            if (i != 0) {
                grau++;
            }
        }
        return grau;
    }

    /**
     * Retorna o número total de vértices
     */
    @Override
    public int numeroDeVertices() {
        return numVertices;
    }

    /**
     * Retorna o número total de arestas
     */
    @Override
    public int numeroDeArestas() {
        return numArestas;
    }

    /**
     * Retorna uma lista de vértices adjacentes
     */
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        if (vertice == null) {
             throw new Exception("Vértice nulo.");
        }
        
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        for (int i = 0; i < numArestas; i++) {
            if (linhaVertice.get(i) < 0 || linhaVertice.get(i) == 2.0) {
                Aresta aresta = arestas.get(i);
                adjacentes.add(aresta.destino());
            }
        }
        return adjacentes;
    }

    /**
     * Altera o peso da primeira aresta encontrada entre a origem e o destino
     */
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                a.setarPeso(peso);
                return;
            }
        }
        throw new Exception("Aresta não encontrada para setar peso.");
    }

    /**
     * Retorna uma coleção de TODAS as arestas entre origem e destino
     */
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        if (origem == null || destino == null) {
             throw new Exception("Vértice de origem ou destino nulo.");
        }
        
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                arestasEncontradas.add(a);
            }
        }
        return arestasEncontradas;
    }

    /**
     * Retorna a lista original de objetos Vertice
     */
    @Override
    public ArrayList<Vertice> vertices() {
        return this.vertices;
    }

    /**
     * Cria e retorna o grafo transposto G^T
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        Grafo gT = new GrafoMatrizIncidencia(this.vertices);
        for (Aresta a : this.arestas) {
            gT.adicionarAresta(a.destino(), a.origem(), a.peso());
        }
        return gT;
    }
}