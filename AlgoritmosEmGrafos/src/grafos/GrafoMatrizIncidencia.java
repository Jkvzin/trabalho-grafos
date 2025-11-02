package grafos;

import java.util.ArrayList;

public class GrafoMatrizIncidencia implements Grafo {
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
    public void adicionarAresta(Vertice origem, Vertice destino) throws Exception {
        // CORREÇÃO: Removido try-catch. Deixa a exceção subir.
        this.adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(Vertice origem, Vertice destino, double peso) throws Exception {
        // CORREÇÃO: Removido try-catch.
        Aresta novaAresta = new Aresta(origem, destino, peso);
        this.arestas.add(novaAresta);

        boolean isLoop = (origem.id() == destino.id());
        for (int v = 0; v < this.numVertices; v++) {
            if (isLoop && v == origem.id()) {
                this.matriz.get(v).add(2.0); // Seu código de loop, está correto
            } else if (v == origem.id()) {
                this.matriz.get(v).add(-1.0); // Sua convenção, está correta
            } else if (v == destino.id()) {
                this.matriz.get(v).add(1.0); // Sua convenção, está correta
            } else {
                this.matriz.get(v).add(0.0);
            }
        }
        this.numArestas++;
    }

    @Override
    public boolean existeAresta(Vertice origem, Vertice destino) throws Exception { // Adicionado throws
        // CORREÇÃO: Removido try-catch.
        // CORREÇÃO: Bug do 'return false' dentro do loop.
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                return true; // Achou!
            }
        }
        return false; // Só retorna false DEPOIS de checar tudo.
    }

    @Override
    public int grauDoVertice(Vertice vertice) throws Exception {
        // CORREÇÃO: Removido try-catch de validação.
        int grau = 0;
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        // Seu código (i != 0) está CORRETO para (Entrada + Saída + Loop)
        for (Double i : linhaVertice) {
            if (i != 0) {
                grau++;
            }
        }
        return grau;
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
        // CORREÇÃO: Removido try-catch de validação.
        ArrayList<Vertice> adjacentes = new ArrayList<>();
        ArrayList<Double> linhaVertice = matriz.get(vertice.id());

        // Seu código (i < 0 || i == 2) está CORRETO para Grau de Saída
        for (int i = 0; i < numArestas; i++) {
            if (linhaVertice.get(i) < 0 || linhaVertice.get(i) == 2.0) {
                Aresta aresta = arestas.get(i);
                adjacentes.add(aresta.destino());
            }
        }
        return adjacentes;
    }

    @Override
    public void setarPeso(Vertice origem, Vertice destino, double peso) throws Exception {
        // CORREÇÃO: Removido try-catch de validação.
        for (Aresta a : this.arestas) {
            if (a.origem().id() == origem.id() && a.destino().id() == destino.id()) {
                a.setarPeso(peso);
                return;
            }
        }
        // Lança exceção se não encontrar (como a interface sugere)
        throw new Exception("Aresta não encontrada para setar peso.");
    }

    @Override
    public ArrayList<Aresta> arestasEntre(Vertice origem, Vertice destino) throws Exception {
        // CORREÇÃO: Removido try-catch de validação.
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

    /**
     * Cria o grafo transposto O(E * V) (pois adicionarAresta é O(V))
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