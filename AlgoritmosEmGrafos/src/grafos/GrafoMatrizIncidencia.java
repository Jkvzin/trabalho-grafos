package grafos;

import java.util.ArrayList;

public class GrafoMatrizIncidencia implements Grafo{
    private ArrayList<Vertice> vertices;
    private ArrayList<Aresta> arestas; // Lista de "colunas"
    private ArrayList<ArrayList<Double>> matriz; // Matriz V x A
    private int numVertices;
    private int numArestas; // Corresponde ao número de colunas

    public GrafoMatrizIncidencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        
        this.arestas = new ArrayList<>();
        this.matriz = new ArrayList<>(this.numVertices);
        
        // Inicializa as V linhas (uma para cada vértice)
        for (int i = 0; i < this.numVertices; i++) {
            this.matriz.add(new ArrayList<>());
        }
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception{
        this.adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        Aresta novaAresta = new Aresta(origem, destino, peso);
        this.arestas.add(novaAresta); 

        boolean isLoop = (origem.id() == destino.id());
        for (int v = 0; v < this.numVertices; v++) {

            if (isLoop && v == origem.id()) {
                this.matriz.get(v).add(2.0);
            } else if (v == origem.id()) {
                // Aresta "normal" saindo
                this.matriz.get(v).add(-1.0); 
            } else if (v == destino.id()) {
                // Aresta "normal" entrando
                this.matriz.get(v).add(1.0); 
            } else {
                // Vértice não é tocado pela aresta
                this.matriz.get(v).add(0.0);
            }
        }
        this.numArestas++; 
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino){
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int grauDoVertice(Vertice vertice) throws Exception{
        int grau = 0;

        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        for(Double i : linhaVertice){
            if (i < 0 || i == 2) {
                grau++;
            }
        }
        return grau;
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
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception{
        ArrayList<Vertice> adjacentes = new ArrayList<>();

        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        for(int i = 0; i < numArestas; i++){
            if (linhaVertice.get(i) < 0 || linhaVertice.get(i) == 2) {
                Aresta aresta = arestas.get(i);
                adjacentes.add(aresta.destino());
            }
        }
        return adjacentes;
    }

    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception{
        // Seta o peso da PRIMEIRA aresta encontrada se existirem duas paralelas
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                a.setarPeso(peso);
                return;
            }
        }
        throw new Exception("Aresta não encontrada entre " + origem.id() + " e " + destino.id());
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                arestasEncontradas.add(a);
            }
        }
        return arestasEncontradas;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        return this.vertices;
    }
}