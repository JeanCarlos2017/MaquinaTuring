package Grafos;

import java.util.ArrayList;

public class ListaAdjacencia {
    public ArrayList<Vertice> listaVertices = new ArrayList<>();
    
    public void addAresta(int ini, int fin, String estado) { 
        Aresta nova = new Aresta(ini, fin, estado, null); 
        Aresta ant = null;
        for(Aresta aux = buscaVertice(ini).getProx(); aux != null; aux = aux.getProx())
            ant = aux;
        
        if (ant == null)
            buscaVertice(ini).setProx(nova);
        else
            ant.setProx(nova);
    }
    
    public ListaAdjacencia(int numVert) {
        for (int i = 0; i < numVert; i++) {
            this.addVertice(i);
        }     
    }
   
    public ListaAdjacencia(){
    }
    
    public Vertice getVertice (int i){
        return this.listaVertices.get(i);
    }
    public Aresta getAdjacente(int numVert) {
        return listaVertices.get(numVert).getProx();
    }
    
    public void imprimeRepresentacao() {
        Aresta aux;
        for (int i=0; i<listaVertices.size(); i++){
             System.out.print(i + "->");
            for(aux = listaVertices.get(i).getProx(); aux != null; aux = aux.getProx()){
                System.out.print("(" + aux.getIni()+ "," + aux.getFin()+ "," + aux.getEstado()+ ") ");
            }
            System.out.print("\n");
        }
    }

    public void addVertice(int id) {
            listaVertices.add(listaVertices.size(), new Vertice(id));
    }

    public void setInicial(int id, boolean inicial) {
        for (Vertice listaVertice : listaVertices) {
            if (listaVertice.getId() == id) {
                listaVertice.setEstadoIn(inicial);
            }
        }
    }

    public void setFinal(int id, boolean vfinal) {
        for (Vertice listaVertice : listaVertices) {
            if (listaVertice.getId()== id) {
                listaVertice.setEstadoFin(vfinal);
            }
        }
    }

    public void removeAresta(int vInicial, int vFinal, String estado) {
        Vertice Inicial = null;
        for (Vertice listaVertice : listaVertices) {
            if (listaVertice.getId() == vInicial) {
                Inicial = listaVertice;
                break;
            }
        }
        
        Aresta ant = null;
        for(Aresta aux=Inicial.getProx(); aux != null; aux = aux.getProx()){
            if ((aux.getFin()== vFinal) && (aux.getEstado().equals(estado))){
                if (ant == null)
                    Inicial.setProx(aux.getProx());
                else
                    ant.setProx(aux.getProx());
            }
            else
                ant = aux;  
        }     
    }

    public void removeVertice(int id) {
        for (int i = 0; i < listaVertices.size(); i++)
            if (listaVertices.get(i).getId()== id){
                listaVertices.remove(i);
                break;
            }
    }
    
    public Vertice encontraVertice(int vID) {
        for (Vertice listaVertice : listaVertices) {
            if (listaVertice.getId() == vID) {
                return listaVertice;
            }
        }
        return null;
    }
    
    public Vertice buscaVertice(int id) {
        for(int i=0; i < listaVertices.size(); i++)
            if (listaVertices.get(i).getId()== id)
                return listaVertices.get(i);
        return null;
    }
}
