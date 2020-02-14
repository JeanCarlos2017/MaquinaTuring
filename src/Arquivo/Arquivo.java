package Arquivo;
import Grafos.desenho.Edge;
import Grafos.desenho.Graph;
import Grafos.desenho.IUMTuring.Painel;
import Grafos.desenho.Vertex;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

public class Arquivo {
    
    public static void salvarArquivo(String nomeArquivo, Graph graph) throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        //nomeArquivo +=".jff"; //acrescentanto a extensão do jflap 
        ArrayList<Vertex> vertex = graph.getVertex();
        int tamVertex = vertex.size();
        ArrayList<Edge> edges = graph.getEdges();
        int tamEdge = edges.size();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
       
        //cabeçalho
        Element structure = doc.createElement("structure");
        doc.appendChild(structure);
        //type
        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode("turing"));
        structure.appendChild(type);
        //automaton
        Element automaton = doc.createElement("automaton");
        structure.appendChild(automaton);
        
        //escrevendo os estados
         Element isEstInicial = doc.createElement("initial");
         Element isEstFinal = doc.createElement("final");
        for (int i = 0; i < tamVertex; i++){
            Vertex v = vertex.get(i);
                       
            Element block = doc.createElement("block");
            Attr id = doc.createAttribute("id");
            id.setValue(String.valueOf(v.getId()));
            Attr name = doc.createAttribute("name");
            name.setValue(v.getName());
            block.setAttributeNode(id);
            block.setAttributeNode(name);
            //criando os atributos dos estados
            Element tag = doc.createElement("tag");
            tag.appendChild(doc.createTextNode("Machine"+v.getId()));
            block.appendChild(tag);
            Element x = doc.createElement("x");
            x.appendChild(doc.createTextNode(String.valueOf(v.getX())));
            block.appendChild(x);
            Element y = doc.createElement("y");
            y.appendChild(doc.createTextNode(String.valueOf(v.getY())));
            block.appendChild(y);
          
            
            if (v.isInicial()) block.appendChild(isEstInicial);
            if (v.isFinal()) block.appendChild(isEstFinal);
            //colocando a tag automaton como seu pai
            automaton.appendChild(block);
        }
        
        //pegando as arestas 
         for (int i = 0; i < tamEdge; i++){
             Edge e = edges.get(i);
             String label = e.getLabel();
             //retira os espaços e a virgula
             label = label.replace(",", "");
             label = label.replace(" ", "");
             int cont = 0,
                 tamLabel = label.length();
             char read, write, move, descarte;
             
             while (cont < tamLabel){
                 read = label.charAt(cont);
                 cont++;
                 write = label.charAt(cont);
                 cont++;
                 move = label.charAt(cont);
                 cont++;
                 descarte = e.getLabel().charAt(cont);//"|"
                 cont++;
                 Element transition = doc.createElement("transition");
                 Element from = doc.createElement("from");
                 from.appendChild(doc.createTextNode(String.valueOf(e.getOrigem().getId())));
                 transition.appendChild(from);
                 Element to = doc.createElement("to");
                 to.appendChild(doc.createTextNode(String.valueOf(e.getDestino().getId())));
                 transition.appendChild(to);
                 //read
                 Element readElement = doc.createElement("read");
                 readElement.appendChild(doc.createTextNode(String.valueOf(read)));
                 transition.appendChild(readElement);
                 //write
                 Element writeElement = doc.createElement("write");
                 writeElement.appendChild(doc.createTextNode(String.valueOf(write)));
                 transition.appendChild(writeElement);
                 //move
                 Element moveElement = doc.createElement("move");
                 moveElement.appendChild(doc.createTextNode(String.valueOf(move)));
                 transition.appendChild(moveElement);
                 automaton.appendChild(transition);
             }
             
            
             
             
         }
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = tfactory.newTransformer();
        
        DOMSource fonte = new DOMSource(doc);
        StreamResult docFinal = new StreamResult(nomeArquivo+".jff");
        transformer.transform(fonte, docFinal);
    }
    
    public static void lerArquivos(String caminhoArquivo, Graph graph, Painel view) throws IOException{
                        
        //caminhoArquivo = "C:\\Users\\Admin\\Documents\\9 semestre\\Teoria\\exercicios jflap\\10.04\\testeMore.jff";
        //caminhoArquivo = new File("../Teoria da Computação - Maquina Turing/teste pronto.jff").getCanonicalPath();
       
        //caminhoArquivo = "C:\\Users\\Jean\\Desktop\\exercício3.5f.jff";
       // caminhoArquivo = "C:\\Users\\Admin\\Desktop\\atividade3.5.f.jff";
        System.out.println(caminhoArquivo);
         try {
             //para os estados
             float x = 0, y = 0; //posição x e y
             boolean isInicial = false, isFinal = false;
             String id,name;
             //para as arestas
             int origem = 0, destino = 0;
             Vertex origemVertex, destinoVertex;
             String read = null,write = null,move = null,estado;
            //objetos para construir e fazer a leitura do documento
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            //abre e faz o parser de um documento xml de acordo com o nome passado no parametro
            //Document doc = builder.parse("D:\\pessoa.xml");
            Document doc = builder.parse(caminhoArquivo);
            //cria uma lista de estados. Busca no documento todas as tag estados ("block")
            NodeList listaDeEstados = doc.getElementsByTagName("block");
            
            //pego o tamanho da lista de estados
            int tamanhoLista = listaDeEstados.getLength();
            
            //varredura na lista de estados
            for (int i = 0; i < tamanhoLista; i++) {
                
                //pego cada item (estados) como um nó (node)
                Node noEstado = listaDeEstados.item(i);
                
                //verifica se o noEstado é do tipo element (e não do tipo texto etc)
                if(noEstado.getNodeType() == Node.ELEMENT_NODE){
                    
                    //caso seja um element, converto o no Pessoa em Element pessoa
                    Element elementoEstado = (Element) noEstado;
                    
                    //já posso pegar o atributo do element
                    id = elementoEstado.getAttribute("id");
                    name = elementoEstado.getAttribute("name");
                      
                  
                    //recupero os nos filhos do elemento estado (x, y, isInicial e isFinal)
                    NodeList listaDeFilhosDeEstado = elementoEstado.getChildNodes();
                    //pego o tamanho da lista de filhos do elemento estado (2 ou 3)
                    int tamanhoListaFilhos = listaDeFilhosDeEstado.getLength();
                            
                    //varredura na lista de filhos do elemento estado
                    for (int j = 0; j < tamanhoListaFilhos; j++) {
                        
                        //crio um no com o cada tag filho dentro do no pessoa (tag nome, idade e peso)
                        Node noFilho = listaDeFilhosDeEstado.item(j);
                        
                        //verifico se são tipo element
                        if(noFilho.getNodeType() == Node.ELEMENT_NODE){
                            
                            //converto o no filho em element filho
                            Element elementoFilho = (Element) noFilho;
                            
                            //verifico em qual filho estamos pela tag
                            switch(elementoFilho.getTagName()){
                                case "x":
                                    //pega a posição em x 
                                    x = Float.parseFloat (elementoFilho.getTextContent());
                                    break;
                                    
                                case "y":
                                    //pega a posição em y
                                    y = Float.parseFloat (elementoFilho.getTextContent());
                                    break;
                                    
                                case "initial":
                                    //é inicial
                                    isInicial = true;
                                    break;
                                case "final":
                                    // é final 
                                    isFinal = true;
                                    break;
                            }
                        }
                    }
                    //agora tenho os atributos referentes ao estado
                    Vertex vertex = new Vertex(x, y, Integer.parseInt(id),name, isInicial, isFinal);
                    graph.addVertex(vertex);
                    if (isInicial) graph.setInicial(graph.getVertexId(Integer.parseInt(id)), true);
                    if (isFinal) graph.setFinal(graph.getVertexId(Integer.parseInt(id)), true);
                    isInicial = isFinal = false;
                    view.cleanImage();
                    view.repaint();
                }
            }
            
            //tratamento para pegar as arestas 
            NodeList listaDeArestas = doc.getElementsByTagName("transition");
            
            //pego o tamanho da lista de arestas
            tamanhoLista = listaDeArestas.getLength();
            
            //varredura na lista de arestas
            for (int i = 0; i < tamanhoLista; i++) {
                Node noAresta = listaDeArestas.item(i);
                
                //verifica se o noAresta é do tipo element (e não do tipo texto etc)
                if(noAresta.getNodeType() == Node.ELEMENT_NODE){
                    
                    //caso seja um element, converto o no aresta em Element aresta
                    Element elementoAresta = (Element) noAresta;
                    //recupero os nos filhos do elemento aresta (from,to, read, write, move)
                    NodeList listaDeFilhosDeAresta = elementoAresta.getChildNodes();
                    //pego o tamanho da lista de filhos do elemento estado (2 ou 3)
                    int tamanhoListaFilhos = listaDeFilhosDeAresta.getLength();
                            
                    //varredura na lista de filhos do elemento estado
                    for (int j = 0; j < tamanhoListaFilhos; j++) {
                        
                        //crio um no com o cada tag filho dentro do no pessoa (tag nome, idade e peso)
                        Node noFilho = listaDeFilhosDeAresta.item(j);
                        
                        //verifico se são tipo element
                        if(noFilho.getNodeType() == Node.ELEMENT_NODE){
                            
                            //converto o no filho em element filho
                            Element elementoFilho = (Element) noFilho;
                            
                            //verifico em qual filho estamos pela tag
                            switch(elementoFilho.getTagName()){
                                case "from":
                                    //pega a origem
                                    origem = Integer.parseInt(elementoFilho.getTextContent());
                                    break;
                                    
                                case "to":
                                    //pega o destino
                                     destino = Integer.parseInt(elementoFilho.getTextContent());
                                    break;
                                    
                                case "read":
                                    //o simbolo lido
                                    read = elementoFilho.getTextContent();
                                    if (read.equals("")) read = "\u03BB";
                                    break;
                                case "write":
                                    //símbolo escrito
                                    write = elementoFilho.getTextContent();
                                    if (write.equals("")) write = "\u03BB";
                                    break;
                                case "move":
                                    move = elementoFilho.getTextContent();
                                    break;
                            }
                        }
                    }
                    estado = read + "," + write + "," + move;
                    //agora tenho os atributos referentes a aresta
                    origemVertex = graph.getVertexId(origem);
                    destinoVertex = graph.getVertexId(destino);
                    //graph.getVertexId(Integer.parseInt(id))
                    graph.addEdge(new Edge(origemVertex, destinoVertex, estado));
                    //estado = "";
                    view.cleanImage();
                    view.repaint();
                }
            }
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Arquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
