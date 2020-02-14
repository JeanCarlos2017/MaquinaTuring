package Grafos.desenho;

import Grafos.Aresta;
import Grafos.Grafo;
import Grafos.ListaAdjacencia;
import java.util.ArrayList;
import java.util.Iterator;

public class Graph {
    public int numVertices;
    protected ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<Vertex> vertex = new ArrayList<>();
    private int vert = 0;
    public Grafo grafo;

    public Graph() {
        this.numVertices = 0;
        grafo = new Grafo();
    }    
    
    public ListaAdjacencia getListaVertices(){
        return this.grafo.getLista();
    }
    
    public ArrayList<Vertex> getListaVertexVisitados(){
        ArrayList<Vertex> caminhoVertex = new ArrayList<>();
        Vertex v;
        for (Integer i: grafo.getListaVertexId()){
            v = this.findVertex(i);
            caminhoVertex.add(v);
        }
        return caminhoVertex;
    }
   
    public void addVertex(float x, float y){
        this.setVert();
        grafo.addVertice(vert);
        this.vertex.add(new Vertex(vert, "q"+vert, x, y));
        vert++;
        this.numVertices++;
    }
    
    public void addVertex(int id, String name, String label, float x, float y){
        grafo.addVertice(id);
        this.vertex.add(new Vertex(id, name, label, x, y));
        this.numVertices++;
        this.setVert();
    }
    
    public void addVertex (Vertex v){
        grafo.addVertice(v.getId());
        this.vertex.add(v);
        this.numVertices++;
        this.setVert();
    }
   
    public void setVert(){
        int maior = 0;
        int ant;
        boolean achou = false;
        int tamanho = this.vertex.size();
        
        while(!achou){
            ant = maior;
            for (Vertex vertex1 : this.vertex) {
                if (vertex1.getId() == maior) {
                    maior++;
                    break;
                }
            }
            if (maior == ant)
                achou = true;
        }
        vert = maior;
    }

    public int getVert() {
        return vert;
    }
    
    public void delAllVertex(){
       Vertex v;
       while (this.vertex.size() > 0){
           v = this.vertex.get(0);
           this.delVertex(v);
           }
    }
    public void delVertex(Vertex v) {
        for (int j = 0; j < this.edges.size();) {
            if ((this.edges.get(j).getOrigem().equals(v))
                    || (this.edges.get(j).getDestino().equals(v))) {
                this.delEdge(this.edges.get(j));
            } else {
                j++;
            }
        }
        this.vertex.remove(v);
        grafo.getLista().removeVertice(v.getId());
        this.delNumVertices();
    }

    public void addEdge(Edge e){
        Edge aux = verificaEdgesIguais(e);
        if (aux == null)
            this.edges.add(e);
        else{
            aux.setLabel(aux.getLabel() + " | " + e.getLabel());
        }
        grafo.addAresta(e.getOrigem().getId(), e.getDestino().getId(), e.getLabel());
            
    }
    
    public void delEdge(Edge e){
        this.edges.remove(e);
        grafo.getLista().removeAresta(e.getOrigem().getId(), e.getDestino().getId(), e.getLabel());
    }
    
    
    public final void computeCircledPosition(int ray){
        int nVertices = this.vertex.size();
        int step = 360 / nVertices;
        int deslocX = 100 + ray;
        int deslocY = 100 + ray;
        for (int i=0; i<nVertices; i++){
            double ang = i * step;
            ang = ang * Math.PI / 180;
            float X = (float) Math.cos(ang);
            float Y = (float) Math.sin(ang);
            X = X * ray + deslocX;
            Y = Y * ray + deslocY;
            this.vertex.get(i).setX(X);
            this.vertex.get(i).setY(Y);
        }
    }

    public ArrayList<Vertex> getVertex() {
        return this.vertex;
    }

    public void draw(java.awt.Graphics2D g2) {
        
        edges.stream().forEach((edge) -> {
            edge.draw(g2, edges);
        });
        this.vertex.stream().forEach((v) -> {
            v.draw(g2);
        });
        
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public java.awt.Dimension getSize() {
        if (this.vertex.size() > 0) {
            float maxX = vertex.get(0).getX();
            float minX = vertex.get(0).getX();
            float maxY = vertex.get(0).getY();
            float minY = vertex.get(0).getY();

            for (Vertex v : this.vertex) {
                if (maxX < v.getX()) {
                    maxX = v.getX();
                } else {
                    if (minX > v.getX()) {
                        minX = v.getX();
                    }
                }

                if (maxY < v.getY()) {
                    maxY = v.getY();
                } else {
                    if (minY > v.getY()) {
                        minY = v.getY();
                    }
                }
            }

            int w = (int) (maxX + (this.vertex.get(0).getRay() * 5)) + 350;
            int h = (int) (maxY + (this.vertex.get(0).getRay() * 5));

            return new java.awt.Dimension(w, h);
        } else {
            return new java.awt.Dimension(0, 0);
        }
    }
    
    public void delNumVertices() {
        this.numVertices--;
    }

    public int getNumVertices() {
        return numVertices;
    }
    
    public Vertex getVertexId (int id){
        for (Vertex v: this.vertex){
            if (v.getId() == id) return v;
        }
        return null;
    }
    public Vertex findVertex(int vID) {
        for(int i=0; i < this.getVertex().size(); i++)
            if (this.getVertex().get(i).getId() == vID)
                return this.getVertex().get(i);
        return null;
    }
   
    public Vertex getVertex(int i) {
        return this.vertex.get(i);
    }

    private Edge verificaEdgesIguais(Edge e) {
        for(int i=0; i < getEdges().size(); i++){
            Edge aux = this.getEdges().get(i);
            if ((aux.getOrigem()== e.getOrigem()) && (aux.getDestino()== e.getDestino()))
                return aux;
        }
        return null;
    }
    
    boolean delEdge(Vertex origem, Vertex destino, String label) {
        
        if (grafo.delAresta(origem.getId(), destino.getId(), label) == false)
            return false;
        
        Edge edg = null;
        for (Iterator<Edge> it = this.getEdges().iterator(); it.hasNext();) {
            edg = it.next();
            if((edg.getOrigem().getId()==origem.getId()) && (edg.getDestino().getId() == destino.getId()))
                break;
        }
        
        if (edg == null)
            return false;
        
        String newLabel = null;
        for(Aresta aux=grafo.getLista().encontraVertice(origem.getId()).getProx(); aux != null; aux = aux.getProx()){
            if (aux.getFin()== destino.getId()){
                if (newLabel == null)
                    newLabel = aux.getEstado();
                else
                    newLabel = newLabel.concat(" | ").concat(aux.getEstado());
            }
        }
        if (newLabel != null)
            edg.setLabel(newLabel);
        else
            getEdges().remove(edg);
        
        return true;      
    }
            
    public void setInicial(Vertex v, boolean isInicial){
        v.setInicial(isInicial);
        grafo.getLista().setInicial(v.getId(), isInicial);
    }

    public void setFinal(Vertex v, boolean isFinal) {
        v.setFinal(isFinal);
        grafo.getLista().setFinal(v.getId(), isFinal);
    }
    
    public ArrayList<Edge> findVertexFromInEdge(Vertex v){
        ArrayList<Edge> arestas = new ArrayList<>();
        for (int i = 0; i < this.edges.size();i++){
            if (this.edges.get(i).getOrigem() == v) 
                arestas.add(this.edges.get(i));
        }
        return  edges;
    }
    
    public int getXMaxVertex(){
        int max = 632;
        for (int i = 0; i < this.vertex.size();i++){
            if (this.vertex.get(i).getX() > max) 
                max = (int) Math.ceil(this.vertex.get(i).getX());
        }
        return  max;
    }
    public int getYMaxVertex(){
        int max = 431;
        for (int i = 0; i < this.vertex.size();i++){
            if (this.vertex.get(i).getY() > max) 
                max = (int) Math.ceil(this.vertex.get(i).getY());
        }
        return  max;
    }
}
