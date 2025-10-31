package grafos;

import java.util.ArrayList;



// Mudar o jeito que coloquei os EXCEPTIONS??? Pensar nisso e pedir opiniões

public class GrafoMatrizIncidencia implements Grafo{
    private ArrayList<Vertice> vertices;
    private ArrayList<Aresta> arestas; // Lista de "colunas"
    private ArrayList<ArrayList<Double>> matriz; // Matriz V x A
    private int numVertices;
    private int numArestas;

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

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception{
        try {
            this.adicionarAresta(origem, destino, 1.0);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }



    //EXCEPTIONS
    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        try{
            Aresta novaAresta = new Aresta(origem, destino, peso);
            this.arestas.add(novaAresta); 

            boolean isLoop = (origem.id() == destino.id());
            for (int v = 0; v < this.numVertices; v++) {

                if (isLoop && v == origem.id()) {
                    this.matriz.get(v).add(2.0);
                } else if (v == origem.id()) {
                    this.matriz.get(v).add(-1.0); 
                } else if (v == destino.id()) {
                    this.matriz.get(v).add(1.0); 
                } else {
                    this.matriz.get(v).add(0.0);
                }
            }
            this.numArestas++; 
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino){
        try {
            if (origem == null || destino == null) {
                throw new Exception("Não foi possível encontrar o vertice de origem ou de destino, verifique se escolheu um vértice que realmente existe no grafo.");
            }
            for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                return true;
            }
            return false;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public int grauDoVertice(Vertice vertice) throws Exception{
        try {
            if (vertice == null) {
                throw new Exception("Vertice não encontrado, verifique se escolheu um vértice que realmente existe no grafo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        int grau = 0;

        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        for(Double i : linhaVertice){
            if (i != 0) { // <-- Isso já conta entrada (+1) e saída (-1 e 2)
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
        try {
            if (vertice == null) {
                throw new Exception("Vertice não encontrado, verifique se escolheu um vértice que realmente existe no grafo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            if (origem == null || destino == null) {
                throw new Exception("Não foi possível encontrar o vertice de origem ou de destino, verifique se escolheu um vértice que realmente existe no grafo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                a.setarPeso(peso);
                return;
            }
        }
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        try {
            if (origem == null || destino == null) {
                throw new Exception("Não foi possível encontrar o vertice de origem ou de destino, verifique se escolheu um vértice que realmente existe no grafo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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