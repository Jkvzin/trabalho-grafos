package grafos;

import java.util.ArrayList;

/**
 * Implementação da interface Grafo usando a estrutura de Lista de Adjacência
 * Nesta estrutura, o grafo é representado por um ArrayList de listas. ArrayList de ArrayList de arestas
 * O índice 'i' do array principal corresponde ao vértice de ID 'i'
 * O conteúdo de listaAdjacencia.get(i) é uma lista de todas as arestas que *saem* do vértice 'i'
 */
public class GrafoListaAdjacencia implements Grafo {
    
    // Armazena a lista de todos os vértices (objetos) do grafo
    private ArrayList<Vertice> vertices;
    // ArrayList de ArrayList de Arestas
    private ArrayList<ArrayList<Aresta>> listaAdjacencia;
    private int numArestas;
    private int numVertices;

    /**
     * Construtor
     * @param vertices Uma lista de todos os vértices que vão compor o grafo
     */
    public GrafoListaAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        
        // Inicializando a lista externa com o tamanho do número de vértices
        this.listaAdjacencia = new ArrayList<>(this.numVertices);
        
        // Para cada vértice se cria uma lista interna vazia para armazenar suas arestas
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
     * Verifica se existe pelo menos uma aresta entre uma origem e um destino
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
     * Calcula o grau total do vértice (entrada + saida)
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        // Calcula o grau de saída
        int grauSaida = this.listaAdjacencia.get(vertice.id()).size();
        
        // Calcula o grau de entrada
        int grauEntrada = 0;
        for (ArrayList<Aresta> listaDeOutroVertice : this.listaAdjacencia) {
            for (Aresta aresta : listaDeOutroVertice) {
                if (aresta.destino().id() == vertice.id()) {
                    grauEntrada++;
                }
            }
        }
        
        return grauSaida + grauEntrada;
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
     * Se houver arestas paralelas (ex: duas de A para B), o vértice B aparecerá duas vezes na lista
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
     * Altera o peso da primeira aresta que encontra entre a origem e o destino
     */
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        for (Aresta aresta : arestasVertice) {
            if (aresta.destino().id() == destino.id()) {
                aresta.setarPeso(peso);
                return;
            }
        }
    }
    
    /**
     * Retorna uma coleção de todas as arestas entre origem e destino
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
     * Retorna a lista de vertices.
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
        // Cria um novo grafo vazio com os mesmos vértices
        Grafo gT = new GrafoListaAdjacencia(this.vertices);
        
        // Itera por todas as arestas (A) do grafo original
        for (ArrayList<Aresta> listaDoVerticeU : this.listaAdjacencia) {
            for (Aresta a : listaDoVerticeU) {
                // Adiciona a aresta (v, u) no grafo transposto
                gT.adicionarAresta(a.destino(), a.origem(), a.peso());
            }
        }
        return gT;
    }
}