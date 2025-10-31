package grafos;

import java.util.ArrayList;

public class GrafoListaAdjacencia implements Grafo{
    private ArrayList<Vertice> vertices;
    private ArrayList<ArrayList<Aresta>> listaAdjacencia;
    private int numArestas;
    private int numVertices;

    public GrafoListaAdjacencia (ArrayList<Vertice> vertices){
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
        
        // Pega a lista de arestas do vértice de origem e adiciona a nova aresta
        this.listaAdjacencia.get(origem.id()).add(novaAresta);
        this.numArestas++;
    }
    
    @Override
    public boolean existeAresta(Vertice origem, Vertice destino){
    
        ArrayList<Aresta> arestasOrigem = listaAdjacencia.get(origem.id());

        for(Aresta aresta : arestasOrigem){
            if (aresta.destino() == destino) {
                return true;
            }
        }
        return false;
    }
    

    //VERIFICAR TAMBÉM AS ARESTAS QUE ENTRAM NELE
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        // 1. Grau de Saída (rápido)
        int grauSaida = this.listaAdjacencia.get(vertice.id()).size();
        
        // 2. Grau de Entrada (lento, O(V+E))
        int grauEntrada = 0;
        for (ArrayList<Aresta> listaDeOutroVertice : this.listaAdjacencia) {
            for (Aresta aresta : listaDeOutroVertice) {
                if (aresta.destino() == vertice) {
                    grauEntrada++;
                }
            }
        }
        
        return grauSaida + grauEntrada;
    }
    
    @Override
    public int numeroDeVertices(){
        return numVertices;
    }
    
    @Override
    public int numeroDeArestas(){
        return numArestas;
    }
    
    @Override
    /**
     * Se existirem arestas paralelas ele vai adicionar duas vezes / não atrapalha mas é redundante
     */
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception{
        try {
            if (vertice == null) {
                throw new Exception("Vertice não encontrado, verifique se escolheu um vértice que realmente existe no grafo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(vertice.id());
        for(Aresta aresta : arestasVertice){
            adjacentes.add(aresta.destino());
        }
        return adjacentes;
    }
    
    @Override
    /**
     * seta o peso da primeira aresta que encontrar
     */
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception{
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        for(Aresta aresta : arestasVertice){
            if(aresta.destino() == destino){
                aresta.setarPeso(peso);
                return;
            }
        }
    }
    
    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception{
        ArrayList<Aresta> arestasEntre = new ArrayList<>();
        ArrayList<Aresta> arestasVertice = listaAdjacencia.get(origem.id());
        for(Aresta aresta : arestasVertice){
            if(aresta.destino() == destino){
                arestasEntre.add(aresta);
            }
        }
        return arestasEntre;
    }
    
    @Override
    public ArrayList<Vertice> vertices(){
        return vertices;
    }
    
}
