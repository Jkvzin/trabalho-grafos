package grafos;

import java.util.ArrayList;

// Versão PADRÃO da Matriz de Adjacência
// NÃO suporta arestas paralelas, mas é O(1) para a maioria das operações.

public class GrafoMatrizAdjacencia implements Grafo {
    private ArrayList<Vertice> vertices;
    // A matriz armazena o PESO. Double (objeto) permite usar 'null'
    private Double[][] matriz; 
    private int numVertices;
    private int numArestas;

    public GrafoMatrizAdjacencia(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        this.numVertices = vertices.size();
        this.numArestas = 0;
        this.matriz = new Double[numVertices][numVertices];
        // A matriz já começa com 'null' em todas as posições
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        this.adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        int u = origem.id();
        int v = destino.id();

        // Se não havia aresta, incrementa o contador
        if (this.matriz[u][v] == null) {
            this.numArestas++;
        }
        // Armazena o PESO. Se já existia, sobrescreve.
        this.matriz[u][v] = peso; 
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) {
        return this.matriz[origem.id()][destino.id()] != null;
    }


    //Verificar depois se dá certo
    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        int grau = 0;
        int u = vertice.id();
        for (int v = 0; v < numVertices; v++) {
            if (this.matriz[u][v] != null) {
                grau++;
            }
            if (this.matriz[v][u] != null) {
                grau++;
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
        if (!this.existeAresta(origem, destino)) {
            throw new Exception("Aresta não encontrada.");
        }
        // Operação O(1) - Rápida
        this.matriz[origem.id()][destino.id()] = peso;
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();
        Double peso = this.matriz[origem.id()][destino.id()];

        // Operação O(1) - Rápida
        if (peso != null) {
            arestasEncontradas.add(new Aresta(origem, destino, peso));
        }
        return arestasEncontradas;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        return vertices;
    }
}



/*package grafos;

import java.util.ArrayList;

//Só pode armazenar grafos sem arestas com valor e paralelas, perguntar pro Castilho se precisa melhorar isso.
//Fazer colocando o peso num arraylist de arestas? Perguntar pro João Guilherme
//Fácil de mudar

public class GrafoMatrizAdjacencia implements Grafo{
    private ArrayList<Vertice> vertices;
    private ArrayList<Aresta> arestas;
    private Double[][] matriz;
    private int numVertices;
    private int numArestas;

    public GrafoMatrizAdjacencia(ArrayList<Vertice> vertices){
        this.vertices = vertices;
        this.arestas = new ArrayList<Aresta>();
        numVertices = vertices.size();
        matriz = new Double[numVertices][numVertices];
        numArestas = 0;
        
        for(int i = 0; i < numVertices; i++){
            for(int j = 0; j < numVertices; j++){
                matriz[i][j] = 0.0;
            }
        }
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception{
        this.adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception{
        int u = origem.id();
        int v = destino.id();
        Aresta aresta = new Aresta(origem, destino, peso);
        arestas.add(aresta);

        this.numArestas++;

        this.matriz[u][v] += 1.0;
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino){
        if(matriz[origem.id()][destino.id()] != 0.0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        int grau = 0;
        int u = vertice.id();
        for (int v = 0; v < numVertices; v++){
            grau += matriz[u][v];
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
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) throws Exception{
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        int u = vertice.id();
        for (int v = 0; v < numVertices; v++){
            if(this.matriz[u][v] != 0){
                adjacentes.add(vertices.get(v));
            }
        }
        return adjacentes;
    }

    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception{
        if (!this.existeAresta(origem, destino)) {
            throw new Exception("Aresta não encontrada entre " + origem.id() + " e " + destino.id());
        }
        for(Aresta aresta : arestas){
            if(aresta.origem() == origem && aresta.destino() == destino){
                aresta.setarPeso(peso);
                return;
            }
        }
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        ArrayList<Aresta> arestasEncontradas = new ArrayList<>();

        for(Aresta aresta : arestas){
            if(aresta.origem() == origem && aresta.destino() == destino){
                arestasEncontradas.add(aresta);
            }
        }
        return arestasEncontradas;
    }

    @Override
    public ArrayList<Vertice> vertices(){
        return vertices;
    }
}




 */