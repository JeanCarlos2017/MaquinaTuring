package Grafos;

import Grafos.desenho.Edge;
import Grafos.desenho.Graph;
import Grafos.desenho.Vertex;
import Model.Turing.MaquinaTuring;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class Grafo {
    private int numVertices;
    private ListaAdjacencia listaAdj;
    //guardar o caminho
    private ArrayList<Integer> listaVertexId; //vertices visitados
    private ArrayList<Integer> listaDePosicaoFita; //posições da fita que foram alteradas
    private ArrayList<Aresta> listaDeArestas; //arestas percorridas
    private static int qntPassos;

    public Grafo(){
        this.listaVertexId = new ArrayList();
        this.numVertices = 0;
        this.listaAdj = new ListaAdjacencia();
    }
    
    public Grafo(int numVertices){
        this.listaVertexId = new ArrayList();
        this.numVertices = 0;
        this.listaAdj = new ListaAdjacencia(numVertices);
    }

    public int getQntPassos() {
        return qntPassos;
    }

    public void setQntPassos(int qntPassos) {
        this.qntPassos = qntPassos;
    }
    
    
    
    public void inicializaPassoAPasso(){
        this.listaDeArestas = new ArrayList();
        this.listaDePosicaoFita = new ArrayList();
        this.listaVertexId = new ArrayList();
    }
   
    public ArrayList<Integer> getListaVertexId() {
        return listaVertexId;
    }

    public ArrayList<Integer> getListaDePosicaoFita() {
        return listaDePosicaoFita;
    }
    
    public char getListaDePosicaoFita (int pos, boolean isProxPasso){
        char c;
        Aresta a = listaDeArestas.get(pos);
        String[] s = a.getEstado().split(",");
        if (isProxPasso) c = s[1].charAt(0); //retorna o simbolo escrito
        else c = s[0].charAt(0); //retorna o simbolo lido
        return c;
    }
   
    public ArrayList<Aresta> getListaDeArestas() {
        return listaDeArestas;
    }
    
    public void setNovaListaVertexId() {
        this.listaVertexId = new ArrayList();
    }
    
    public Vertice getVerticeDeListaAdj(int i){
        return this.listaAdj.getVertice(i);
    }
    
    public void addAresta(int ini, int fim, String estado){
        listaAdj.addAresta(ini, fim, estado);
    }
   
    public int getNumVert(){
        return numVertices;
    }
    
    public boolean delAresta(int vIni, int vFin, String estado) {
        Aresta anterior = null;
        for(Aresta aux=listaAdj.buscaVertice(vIni).getProx(); aux != null; aux = aux.getProx()){
            if ((aux.getFin() == vFin) && (aux.getEstado().equals(estado))){
                if (anterior == null)
                    listaAdj.buscaVertice(vIni).setProx(aux.getProx());
                else
                    anterior.setProx(aux.getProx());
                return true;
            }
            anterior = aux;
        }
        return false;
    }
    
    public void addVertice(int ID){
        listaAdj.addVertice(ID);
        numVertices++;
    }
     
    public ListaAdjacencia getLista() {
        return listaAdj;
    }
    
    public void imprimeListaAdjacencia() {
        listaAdj.imprimeRepresentacao();
    }
    
    public boolean verificarTuring(String entrada, MaquinaTuring maquina, boolean multEntradas){
        Vertice inicial = encontraInicial();
        this.inicializaPassoAPasso();
        this.listaVertexId.add(inicial.getId());
        maquina.inicializaFita(entrada);
        return execucaoTuring(inicial.getId(), maquina, 250, 0, multEntradas);
    }
  
    private boolean execucaoTuring(int idVertice, MaquinaTuring maquina, int posicaoFita, int qntPassos, boolean multEntradas){
        Vertice vertice = listaAdj.buscaVertice(idVertice);
        Vertex v;
        boolean bool;
        String simbolos[];
        char simboloLido;
        char simboloEscrito;
        char sentido;
        if (vertice.isEstadoFin()) {
            //passou por um estado final entao reconhece a fita
            //maquina.imprimeFitaNaoVazia();
            if (!multEntradas){
             return this.imprimeRespostaTuring(true, qntPassos);
            }else{
                this.qntPassos = qntPassos;
                return true;
            } 
                
        }
         for (Aresta aux = vertice.getProx(); aux != null; aux = aux.getProx()) {
            
            simbolos = aux.getEstado().split(","); //quebrando o valor da aresta
            //considerando que o tamanho seja 1
             
            simboloLido = simbolos[0].charAt(0);
            if (simbolos[1].equals("\u03BB")) simboloEscrito = 'λ';
            else simboloEscrito = simbolos[1].charAt(0);
            sentido = simbolos[2].charAt(0);
           
            if(simboloLido == maquina.posicaoFita(posicaoFita)){
                    maquina.escreveNaFita(simboloEscrito, posicaoFita);
                    this.listaDePosicaoFita.add(posicaoFita);
                    this.listaVertexId.add(aux.getFin());
                    this.listaDeArestas.add(aux);
                    posicaoFita = maquina.caminhamentoNaFita(posicaoFita, sentido);
                   return this.execucaoTuring(aux.getFin(), maquina, posicaoFita, ++qntPassos,multEntradas);
//                    if (bool){
//                        return bool;
//                    }else{
////                        int backtraking = this.passoAnterior();
////                        if (backtraking != -1){
//                          return execucaoTuring(backtraking, maquina, posicaoFita, ++qntPassos,multEntradas); 
////                        }else return false;
//                    }
                  }
                       
            
         }
        
        if (!multEntradas) return this.imprimeRespostaTuring(false, qntPassos);
        else return false;
        
    }
    private int passoAnterior(){
        int tamanhoListaVertex = this.listaVertexId.size()-1;
        if (tamanhoListaVertex > 0){
            this.listaVertexId.remove(tamanhoListaVertex);
            tamanhoListaVertex--;
            return this.listaVertexId.get(tamanhoListaVertex);
        }
        return -1;
    }
    private boolean imprimeRespostaTuring (boolean resposta, int qntPassos){
        if (resposta){
            this.qntPassos = qntPassos;
          JOptionPane.showMessageDialog(null, "Entrada Válida efetuada em: "+qntPassos+" passos","Máquina Turing", JOptionPane.INFORMATION_MESSAGE);
          return true;
        }
        else{ 
              JOptionPane.showMessageDialog(null, "Entrada Inválida","Máquina Turing", JOptionPane.INFORMATION_MESSAGE);
              return false;
        }
    }
    

    public Vertice encontraInicial(){
        for (Vertice listaVertice : listaAdj.listaVertices) {
            if (listaVertice.isEstadoIn()) {
                return listaVertice;
            }
        }
        return null;
    }
    
    public Vertice encontraFinal(){
        for (Vertice listaVertice : listaAdj.listaVertices) {
            if (listaVertice.isEstadoFin()) {
                return listaVertice;
            }
        }
        return null;
    }

    public boolean passoExecucaoVisita(int vID, String input, Integer[] index, boolean proxVisita[]){
        Vertice v = listaAdj.buscaVertice(vID);
        boolean lambda = false;
                
        for (Aresta aux = v.getProx(); aux != null; aux = aux.getProx())
            if (aux.getEstado().equals("\u03BB"))
                lambda = true;
                
        if ((index[vID] >= input.length()) && (!lambda)) {
            return v.isEstadoFin();
        }

        for (Aresta aux = v.getProx(); aux != null; aux = aux.getProx()) { 
            if (aux.getEstado().equals("\u03BB")){ 
                proxVisita[aux.getFin()] = true; 
                index[aux.getFin()] = index[vID];
            } else{ 
                String saresta = input.substring(index[vID], index[vID] + aux.getEstado().length());
                if (aux.getEstado().equals(saresta)) { 
                    proxVisita[aux.getFin()] = true;
                    index[aux.getFin()] = index[vID] + aux.getEstado().length();
                }
            }
        }
        return false;
    }

    
    
}
