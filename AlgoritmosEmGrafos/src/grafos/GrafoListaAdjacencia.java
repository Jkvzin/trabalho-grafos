package grafos;

import java.util.ArrayList;

public class GrafoListaAdjacencia implements Grafo {
    private ArrayList<Vertice> vertices;
    private ArrayList<ArrayList<Aresta>> listaAdjacencia;
    private int numArestas;
    private int numVertices;

    public GrafoListaAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        
        this.listaAdjacencia = new ArrayList<>(this.numVertices);
        
        for (int i = 0; i < this.numVertices; i++) {
            this.listaAdjacencia.add(new ArrayList<Aresta>());
        }
    }
    
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        adicionarAresta(origem, destino, 1.0);
    }
    
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        Aresta novaAresta = new Aresta(origem, destino, peso);
        this.listaAdjacencia.get(origem.id()).add(novaAresta);
        this.numArestas++;
    }
    
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception { // Adicionado throws
        ArrayList<Aresta> arestasOrigem = listaAdjacencia.get(origem.id());

        for (Aresta aresta : arestasOrigem) {
            // CORREÇÃO: Comparar por ID é mais seguro
            if (aresta.destino().id() == destino.id()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calcula o Grau de Entrada + Grau de Saída.
     * (Como você pediu. AVISO: É lento O(V+E))
     */
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        // 1. Grau de Saída (rápido)
        int grauSaida = this.listaAdjacencia.get(vertice.id()).size();
        
        // 2. Grau de Entrada (lento, O(V+E))
        int grauEntrada = 0;
        for (ArrayList<Aresta> listaDeOutroVertice : this.listaAdjacencia) {
            for (Aresta aresta : listaDeOutroVertice) {
                // CORREÇÃO: Comparar por ID é mais seguro
                if (aresta.destino().id() == vertice.id()) {
                    grauEntrada++;
                }
            }
        }
        
        return grauSaida + grauEntrada;
    }
    
    @Override
    public int numeroDeVertices() {
        return numVertices;
    }
    
    @Override
    public int numeroDeArestas() {
        return numArestas;
    }
    
    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception {
        // (Removido o try-catch que retornava null)
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(vertice.id());
        for (Aresta aresta : arestasVertice) {
            adjacentes.add(aresta.destino());
        }
        return adjacentes;
    }
    
    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        for (Aresta aresta : arestasVertice) {
            // CORREÇÃO: Comparar por ID é mais seguro
            if (aresta.destino().id() == destino.id()) {
                aresta.setarPeso(peso);
                return;
            }
        }
    }
    
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEntre = new ArrayList<>();
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        for (Aresta aresta : arestasVertice) {
            // CORREÇÃO: Comparar por ID é mais seguro
            if (aresta.destino().id() == destino.id()) {
                arestasEntre.add(aresta);
            }
        }
        return arestasEntre;
    }
    
    @Override
    public ArrayList<Vertice> vertices() {
        return vertices;
    }

    /**
     * Cria o grafo transposto O(V+E)
     */
    @Override
    public Grafo criarGrafoTransposto() throws Exception {
        Grafo gT = new GrafoListaAdjacencia(this.vertices);
        
        for (ArrayList<Aresta> listaDoVerticeU : this.listaAdjacencia) {
            for (Aresta a : listaDoVerticeU) {
                gT.adicionarAresta(a.destino(), a.origem(), a.peso());
            }
        }
        return gT;
    }
}