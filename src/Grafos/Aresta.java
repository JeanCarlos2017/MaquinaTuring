package Grafos;

public class Aresta {
    private int ini;
    private int fin;
    private String estado;
    private Aresta prox;

    public Aresta(int ini, int fin, String estado, Aresta prox) {
        this.ini = ini;
        this.fin = fin;
        this.estado = estado;
        this.prox = prox;
    }

    public int getIni() {
        return ini;
    }
    
    public int getFin() {
        return fin;
    }
    
    public String getEstado(){
        return estado;
    }

    public void setIni(int ini) {
        this.ini = ini;
    }
    
    public void setFin(int fin) {
        this.fin = fin;
    }
    
    public void setEstado(String estado){
        this.estado = estado;
    }

    public Aresta getProx() {
        return prox;
    }

    public void setProx(Aresta prox) {
        this.prox = prox;
    }
}