
package Model.Turing;

import Grafos.Aresta;
import Grafos.Vertice;
import Grafos.desenho.Graph;
import javax.swing.JOptionPane;

public class MaquinaTuring {
    private char[] fitaTuring = new char[500];
    private char[] fitaTuringAux = new char[500]; //usada para o passo a passo, armazena a cadeia inicial e depois é tratado as mudanças
    private static int posicaoFita;
    private int qntPassos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private boolean passo;
    private boolean resultado;
    private String entrada;
    
   
    public MaquinaTuring(){
        for (int i = 0; i < 500; i++){
            fitaTuring[i] = 'λ'; //representa o vazio '[]'
            fitaTuringAux [i] = 'λ';
        }
    }

    public char[] getFitaTuring() {
        return fitaTuring;
    }

    public char[] getFitaTuringAux() {
        return fitaTuringAux;
    }
    
    
    public String getEntrada() {
        return entrada;
    }
    
    public void setFitaPos (int pos, char c){
        this.fitaTuring[pos] = c;
    }
    public void inicializaFita(String cadeia){
        for (int i = 0; i < cadeia.length(); i++){
            fitaTuring[250+i] = cadeia.charAt(i); 
            fitaTuringAux[250+i] = cadeia.charAt(i); 
        }
     
    }
    public boolean verificaTuringMultEntradas (Graph graph, String entrada){
         if (graph == null){
           return false;
        }
        
        if (graph.grafo.encontraInicial() == null){
            return false;
        }
        if (graph.grafo.encontraFinal() == null){
            return false;
        }
        return graph.grafo.verificarTuring(entrada, this, true);
    }
    public String fitaTuring(){
        String fita = "";
        for (char a: this.fitaTuring){
            if (a != 'λ')
            fita+=a;
        }
         return fita;   
    }
    public void escreveNaFita (char c, int posicao){
        this.fitaTuring[posicao] = c;
    }

   public char posicaoFita (int posicao){
       return this.fitaTuring[posicao];
   }
    
   public  int caminhamentoNaFita(int posicaoAtual, char sentido){
        
        switch (sentido){
            case 'L': 
                posicaoAtual--;
                break;
            case 'R':
                
                posicaoAtual++;
                break;
            default:
                
        }
        return posicaoAtual;
    }
    
    public  void imprimeFitaNaoVazia(){
        for (char a: fitaTuring)
            if (a != 'λ')
                System.out.print(a);
       
        System.out.println();
    }
  
    
    
  
    private boolean verificaVazio(Graph graph,int vID, boolean lambda) {
        Vertice v = graph.grafo.getLista().encontraVertice(vID);
                
        if (lambda)
            return true;
        
        for (Aresta aux = v.getProx(); aux != null; aux = aux.getProx())
            if (aux.getEstado().equals("\u03BB"))
                return true;
        
        return false;        
    }
    
    
    
    
    public boolean executarTuring(Graph graph, boolean multEntradas) {
        if (graph == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se criar um Autômato!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (graph.grafo.encontraInicial() == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se ter um Estado Inicial!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (graph.grafo.encontraFinal() == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se ter um Estado Final!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        entrada = JOptionPane.showInputDialog(jScrollPane5,"Entrada:");
        
        return graph.grafo.verificarTuring(entrada, this, multEntradas);
         
    } 
     
   
    

}
