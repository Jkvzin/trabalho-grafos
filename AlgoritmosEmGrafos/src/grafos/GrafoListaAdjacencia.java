package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Lista de Adjacência.
 * * Nesta estrutura, o grafo é representado por um array (ou ArrayList) de listas.
 * O índice 'i' do array principal corresponde ao vértice de ID 'i'.
 * O conteúdo de listaAdjacencia.get(i) é uma lista de todas as arestas
 * que *saem* do vértice 'i'.
 */
public class GrafoListaAdjacencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    // A estrutura principal: Uma lista de listas de arestas.
    private ArrayList<ArrayList<Aresta>> listaAdjacencia;
    private int numArestas;
    private int numVertices;

    /**
     * Construtor da classe.
     * @param vertices Uma lista de todos os vértices que existirão no grafo.
     */
    public GrafoListaAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        
        // Inicializa a lista "externa" com o tamanho do número de vértices
        this.listaAdjacencia = new ArrayList<>(this.numVertices);
        
        // Para cada vértice, cria uma lista "interna" vazia para armazenar suas arestas
        for (int i = 0; i < this.numVertices; i++) {
            this.listaAdjacencia.add(new ArrayList<Aresta>());
        }
    }
    
    /**
     * Adiciona uma aresta com peso padrão (1.0).
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        adicionarAresta(origem, destino, 1.0);
    }
    
    /**
     * Adiciona uma aresta com peso específico ao grafo.
     */
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        // Cria o objeto Aresta
        Aresta novaAresta = new Aresta(origem, destino, peso);
        
        // Encontra a lista de adjacência do vértice de origem e adiciona a nova aresta nela
        this.listaAdjacencia.get(origem.id()).add(novaAresta);
        this.numArestas++;
    }
    
    /**
     * Verifica se existe pelo menos uma aresta entre uma origem e um destino.
     */
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception { 
        // Pega a lista de todas as arestas que saem da origem
        ArrayList<Aresta> arestasOrigem = listaAdjacencia.get(origem.id());

        // Procura na lista se alguma aresta aponta para o destino
        for (Aresta aresta : arestasOrigem) {
            if (aresta.destino().id() == destino.id()) {
                return true;
            }
        }
        // Se o loop terminar sem achar, não existe
        return false;
    }
    
    /**
     * Calcula o grau total do vértice (Grau de Entrada + Grau de Saída).
     * Esta implementação é lenta (O(V+E)) para o grau de entrada.
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        // 1. Calcula o Grau de Saída (out-degree)
        // Isso é rápido (O(1)), pois é apenas o tamanho da lista do vértice.
        int grauSaida = this.listaAdjacencia.get(vertice.id()).size();
        
        // 2. Calcula o Grau de Entrada (in-degree)
        // Isso é lento (O(V+E)), pois precisa varrer todas as arestas do grafo.
        int grauEntrada = 0;
        // Itera em todas as listas de adjacência...
        for (ArrayList<Aresta> listaDeOutroVertice : this.listaAdjacencia) {
            // Itera em todas as arestas...
            for (Aresta aresta : listaDeOutroVertice) {
                // Se o destino de uma aresta for o nosso vértice, conta +1.
                if (aresta.destino().id() == vertice.id()) {
                    grauEntrada++;
                }
            }
        }
        
        return grauSaida + grauEntrada;
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
     * Retorna uma lista de vértices adjacentes (vizinhos de saída).
     * Nota: Se houver arestas paralelas (ex: duas de A para B),
     * o vértice B aparecerá duas vezes na lista.
     */
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        // Pega a lista de arestas que saem do vértice
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(vertice.id());
        
        // Adiciona o destino de cada aresta à lista de adjacentes
        for (Aresta aresta : arestasVertice) {
            adjacentes.add(aresta.destino());
        }
        return adjacentes;
    }
    
    /**
     * Altera o peso da primeira aresta encontrada entre a origem e o destino.
     */
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        // Pega a lista de arestas que saem da origem
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        
        for (Aresta aresta : arestasVertice) {
            // Se achar a aresta que aponta para o destino...
            if (aresta.destino().id() == destino.id()) {
                aresta.setarPeso(peso); // ...muda o peso...
                return; // ...e para o método.
            }
        }
    }
    
    /**
     * Retorna uma coleção de TODAS as arestas entre origem e destino.
     * (Suporta grafos com arestas paralelas).
     */
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEntre = new ArrayList<>();
        // Pega a lista de arestas que saem da origem
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        
        for (Aresta aresta : arestasVertice) {
            // Se a aresta aponta para o destino, adiciona à lista de retorno
            if (aresta.destino().id() == destino.id()) {
                arestasEntre.add(aresta);
            }
        }
        return arestasEntre;
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
     * (Todas as arestas (u,v) se tornam (v,u)).
     * Complexidade: O(V+E)
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        // 1. Cria um novo grafo vazio com os mesmos vértices
        Grafo gT = new GrafoListaAdjacencia(this.vertices);
        
        // 2. Itera por todas as arestas (E) do grafo original
        for (ArrayList<Aresta> listaDoVerticeU : this.listaAdjacencia) {
            for (Aresta a : listaDoVerticeU) {
                // 3. Adiciona a aresta (v, u) no grafo transposto
                gT.adicionarAresta(a.destino(), a.origem(), a.peso());
            }
        }
        return gT;
    }
}