package Grafos;

public class Vertice {
    private int id;
    private boolean estadoIn, estadoFin;
    private Aresta prox;

    public Vertice(int id) {
        this.id = id;
        this.prox = null;
        this.estadoIn = false;
        this.estadoFin = false;
    }

    public Aresta getProx() {
        return prox;
    }

    public void setProx(Aresta prox) {
        this.prox = prox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEstadoIn() {
        return estadoIn;
    }

    public void setEstadoIn(boolean estadoIn) {
        this.estadoIn = estadoIn;
    }

    public boolean isEstadoFin() {
        return estadoFin;
    }

    public void setEstadoFin(boolean estadoFin) {
        this.estadoFin = estadoFin;
    }

}