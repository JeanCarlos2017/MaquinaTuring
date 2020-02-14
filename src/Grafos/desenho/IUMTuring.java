
package Grafos.desenho;

import Arquivo.Arquivo;
import Grafos.Vertice;
import Model.Turing.MaquinaTuring;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class IUMTuring extends javax.swing.JFrame {

    public Painel view;
    public Graph graph;
    public Graphics2D g2D;
    public MaquinaTuring maquinaTuring;
    private String caminho = "";
    private Color corPassoAPasso;
    private int qntEstadosVisitados = -1;
    private boolean desenharTransicao=false,
             excluirTransicao = false;
    private Vertice inicial;
    private String fitaAuxiliar; //vai marcar as alterações de acordo com o passo a passo
    private int origem = -1, destino = -1;
    public final static String VAZIO = "\u03BB";
    
    
    
    public IUMTuring() {
        this.view = new Painel();
       this.maquinaTuring = new MaquinaTuring();
       this.jtaFita = new JTextArea();
       this.jtaFita.setEditable(false);
        initComponents();
    }
    
    private void imprimeFitaNaoVazia(boolean isProxPasso){
        int posicaAresta = this.qntEstadosVisitados;
        if (isProxPasso) posicaAresta = this.qntEstadosVisitados -1;
        
        this.jtaFita.setEditable(false);
        if (posicaAresta >= 0){
            int posFita = this.graph.grafo.getListaDePosicaoFita().get(posicaAresta);
            char c = this.graph.grafo.getListaDePosicaoFita(posicaAresta, isProxPasso);
            char chars[] = this.fitaAuxiliar.toCharArray();
            chars[posFita] = c;
            this.fitaAuxiliar = new String (chars); 
            //jtaFita.setText(entrada);
            String a = this.getStringNaoVazia();
            jtaFita.setText(a);
        }
    }
    
    private String getStringNaoVazia(){
        int tam = this.fitaAuxiliar.length();
        String aux = "";
        for (int i = 0; i < tam; i++){
            if (this.fitaAuxiliar.charAt(i) != 'λ')
                aux += this.fitaAuxiliar.charAt(i);
        }
        return aux;
    }

    
    public final class ScrollPainel extends JScrollPane{
        public ScrollPainel(){
            this.setBackground(Color.black);
            this.setSize(100, 100);
            this.setAlignmentX(TOP_ALIGNMENT);
            this.setAlignmentY(TOP_ALIGNMENT);
            this.setAutoscrolls(true);
            this.add(view);
            this.setVisible(true);
        }
    }
    public final class Painel extends JPanel{
        
        public Painel() {            
            this.setBackground(java.awt.Color.white);
            this.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.addMouseListener(new EventoDoMouse());
            this.addMouseMotionListener(new MovimentoDoMouse());
            
        }
        @Override
        public void repaint(){
            super.repaint();
            this.adjustPanel();
        }
        public void adjustPanel() {
            
            if(graph == null || graph.getNumVertices() == 0)
                return;
            
            float iniX = graph.getVertex(0).getX(); 
            float iniY = graph.getVertex(0).getY(); 
            float max_x = iniX, max_y = iniX; 
            float min_x = iniY, min_y = iniY; 
            int zero = (int) graph.getVertex(0).getRay() * 5 + 10; 
 
            for (int i = 1; i < graph.getVertex().size(); i++) { 
                float x = graph.getVertex(i).getX(); 
                if (max_x < x) { 
                    max_x = x; 
                } else if (min_x > x) { 
                    min_x = x; 
                } 
 
                float y = graph.getVertex(i).getY(); 
                if (max_y < y) { 
                    max_y = y; 
                } else if (min_y > y) { 
                    min_y = y; 
                } 
            } 
            
             
            if ((max_x >= 772) && (max_y >= 370)){
                Dimension d = this.getSize(); 
                d.width = (int) max_x + zero; 
                d.height = (int) max_y + zero; 
                this.setSize(d); 
                this.setPreferredSize(d); 
           }
        }
        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
               
            //desenhar estado
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            if (graph != null && this.imageBuffer == null) {
                this.imageBuffer = new BufferedImage(graph.getSize().width + 1,
                        graph.getSize().height + 1, BufferedImage.TYPE_INT_RGB);

                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, graph.getSize().width + 1, graph.getSize().height + 1);

                g2Buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                graph.draw(g2Buffer);
                g2Buffer.setColor(Color.BLACK);
                
                g2Buffer.dispose();
            }
            
            if (this.imageBuffer != null) {
                g2.drawImage(this.imageBuffer, 0, 0, null);
            }
        }
        
        public void cleanImage() {
            this.imageBuffer = null;
        }


        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
        }
        
        public BufferedImage imageBuffer;
     }
    
    public class EventoDoMouse extends MouseAdapter {
        Vertex verticeSelecionado = null;
          private void acessaTransicao(String caixa, boolean isAlterar, int posX, int posY){
            //função adaptada para remover e alter as transições
                //verifica onde soltou o mouse pra encontrar o estado destino
                if ((graph != null) && (!graph.vertex.isEmpty())) {
                    for(int i=graph.vertex.size()-1; i >= 0; i--){
                        float x = graph.vertex.get(i).getX(),
                                y = graph.vertex.get(i).getY(),
                                ray = graph.vertex.get(i).getRay();
                        if ((x + ray > posX) && (x - ray < posX) && (y + ray > posY) && (y - ray < posY)) {
                            destino = i;
                            break;
                        }
                    }
                }
                
                //verifica se soltou o mouse em algum estado
                if(destino != -1){
                    String label = JOptionPane.showInputDialog(jSPAutomato,caixa);
                    if (label.equals(""))
                        label = VAZIO;
                    
                    //label inexistente
                    
                    if (!graph.delEdge(graph.vertex.get(origem), graph.vertex.get(destino), label)){
                        JOptionPane.showMessageDialog(jSPAutomato, "Transição inexistente!", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    excluirTransicao = false;
                    
                    if (isAlterar){
                       //apaga a anterior e desenha a nova
                       label = JOptionPane.showInputDialog(jSPAutomato,"Digite uma nova Transição: "); 
                       graph.addEdge(new Edge(graph.vertex.get(origem), graph.vertex.get(destino), label));
                    }
                    origem = destino = -1;
                    view.cleanImage();
                    view.repaint();
                }
                //se não, não exclui a transição
                else
                    excluirTransicao = false;
        }
   
        @Override  
        public void mouseClicked(MouseEvent e) {
        //criando estados
            if (jMenuItemNovoEstado.isSelected() ){
                if(graph == null)
                    graph = new Graph();

                graph.addVertex(e.getX(), e.getY());
                
                view.cleanImage();
                view.repaint();
            }
         
            //excluindo estados
            if (jMenuItemRemoverEstado.isSelected()){
                if(verticeSelecionado != null)
                    graph.delVertex(verticeSelecionado);
                if(graph.numVertices == 0)
                    graph = null;
            }
          
            //menu com botão direito
            if ((e.getButton() == MouseEvent.BUTTON3)) {

                if (this.getVerticeSelecionado()!= null) { 
                    
                    JPopupMenu menu = new JPopupMenu();
                    
                    //menu estado inicial
                    JMenuItem estInicial = new JCheckBoxMenuItem("Inicial", this.getVerticeSelecionado().isInicial());
                    estInicial.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            if (getVerticeSelecionado().isInicial()== false) {
                                for (int i = 0; i < graph.vertex.size(); i++) {
                                    graph.setInicial(graph.getVertex(i), false);
                                }
                                graph.setInicial(getVerticeSelecionado(), true);
                            } else {
                                graph.setInicial(getVerticeSelecionado(), false);
                            }
                            view.cleanImage();
                            view.repaint();
                        }
                    });
                    menu.add(estInicial);
                    menu.show(view, e.getX(), e.getY());
                    view.cleanImage();
                    view.repaint();
                    
                    //menu estado final
                    JMenuItem estFinal = new JCheckBoxMenuItem("Final", this.getVerticeSelecionado().isFinal());
                    estFinal.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            if (getVerticeSelecionado().isFinal()== false) {
                                graph.setFinal(getVerticeSelecionado(), true);
                                getVerticeSelecionado().setFinal(true);
                            } else {
                                graph.setFinal(getVerticeSelecionado(), false);
                                
                            }
                            view.cleanImage();
                            view.repaint();
                        }
                    });
                    menu.add(estFinal);
                    menu.show(view, e.getX(), e.getY()+1);
                    view.cleanImage();
                    view.repaint();
                    
                    //menu muda label
                    JMenuItem changeLabel = new JMenuItem("Mudar label");
                    changeLabel.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            String label = JOptionPane.showInputDialog("Nova label do estado:", getVerticeSelecionado().getLabel());
                            if(label != null){
                                getVerticeSelecionado().setLabel(label);
                                view.cleanImage();
                                view.repaint();
                            }
                        }
                    });
                    menu.add(changeLabel);
                    menu.show(view, e.getX(), e.getY()+2);
                    view.cleanImage();
                    view.repaint();
                    
                    //menu muda o nome do estado
                    JMenuItem changeName = new JMenuItem("Mudar nome");
                    changeName.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            String name = JOptionPane.showInputDialog("Novo nome do estado:", getVerticeSelecionado().getName());
                            if (name != null){
                                getVerticeSelecionado().setName(name);
                                view.cleanImage();
                                view.repaint();
                            }
                        }
                    });
                    menu.add(changeName);
                    menu.show(view, e.getX(), e.getY()+3);
                    view.cleanImage();
                    view.repaint();
                }
                
            }
            
        } 
        
        @Override
        public void mousePressed(MouseEvent e){
            // selecionando o estado
            if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) {
                if ((graph != null) && (!graph.vertex.isEmpty())) {
                    for(int i=graph.vertex.size()-1; i >= 0; i--){
                        float x = graph.vertex.get(i).getX(),
                                y = graph.vertex.get(i).getY(),
                                ray = graph.vertex.get(i).getRay();
                        if ((x + ray > e.getX()) && (x - ray < e.getX()) && (y + ray > e.getY()) && (y - ray < e.getY())) {
                            setVerticeSelecionado(graph.vertex.get(i));
                            getVerticeSelecionado().setSelected(true);
                            break;
                        }
                    }
                }
            }
            view.cleanImage();
            view.repaint();
            
            
          
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
          
            //tirar a seleção do estado
            if ((graph != null) && (!graph.vertex.isEmpty()) && (this.getVerticeSelecionado() != null) ) {
                this.getVerticeSelecionado().setSelected(false);
                view.cleanImage();
                view.repaint();
            }
            
            //desenhar transição
            if(jMenuItemNovaAresta.isSelected()){
                
                //verifica onde soltou o mouse pra encontrar o estado destino
                if ((graph != null) && (!graph.vertex.isEmpty())) {
                    for(int i=graph.vertex.size()-1; i >= 0; i--){
                        float x = graph.vertex.get(i).getX(),
                                y = graph.vertex.get(i).getY(),
                                ray = graph.vertex.get(i).getRay();
                        if ((x + ray > e.getX()) && (x - ray < e.getX()) && (y + ray > e.getY()) && (y - ray < e.getY())) {
                            destino = i;
                            break;
                        }
                    }
                }
                
                //verifica se soltou o mouse em algum estado
                if(destino != -1){
                    String label = JOptionPane.showInputDialog(jSPAutomato,"Transição: (λ,λ,R)");
                   
                    graph.addEdge(new Edge(graph.vertex.get(origem), graph.vertex.get(destino), label));
                    desenharTransicao = false;
                    origem = destino = -1;
                    view.cleanImage();
                    view.repaint();
                }
                //se não, não cria a transição
                else
                    desenharTransicao = false;
            }
            
            if(jMenuItemRemoverAresta.isSelected()){
               String label = "Digite a transição que deseja excluir: ";
                this.acessaTransicao(label, false, e.getX(), e.getY());
                
            }
            
            if (jMenuItemAlterarAresta.isSelected()){
                String label = "Digite a transição que deseja alterar: ";
                this.acessaTransicao(label, true, e.getX(), e.getY());
            }
        }
        
        public void setVerticeSelecionado(Vertex verticeSelecionado) {
            this.verticeSelecionado = verticeSelecionado;
        }
        
        public Vertex getVerticeSelecionado() {
            return verticeSelecionado;
        }
        
     }
     
     public class MovimentoDoMouse extends MouseMotionAdapter{
         
         @Override
         //arrastar mouse
         public void mouseDragged(MouseEvent e) {
             
            //arrastar estado com botao esquerdo do mouse
            if((graph != null) && (!graph.vertex.isEmpty()) && jMenuItemMoverEstado.isSelected()){
                for(int i=graph.vertex.size()-1; i >= 0; i--){
                    if (graph.vertex.get(i).isSelected()){
                            graph.vertex.get(i).setX(e.getX());
                            graph.vertex.get(i).setY(e.getY());
                            view.cleanImage();
                            view.repaint();
                            break;
                    }
                }
            }
            
            //criar transição  (descobrindo o estado origem)
            if((graph != null) && (!graph.vertex.isEmpty()) && jMenuItemNovaAresta.isSelected()){
                for(int i=graph.vertex.size()-1; i >= 0; i--){
                    if (graph.vertex.get(i).isSelected()){
                        origem = i;
                        break;
                    }
                }
                if (origem != -1)
                    desenharTransicao = true;
            }
            
            //excluir transição (descobrindo o estado de origem)
           if((graph != null) && (!graph.vertex.isEmpty()) && (jMenuItemRemoverAresta.isSelected() || jMenuItemAlterarAresta.isSelected())){
                for(int i=graph.vertex.size()-1; i >= 0; i--){
                    if (graph.vertex.get(i).isSelected()){
                        origem = i;
                        break;
                    }
                }
                if (origem != -1)
                    excluirTransicao = true;
            }
        } 
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jSPAutomato = new javax.swing.JScrollPane(this.view);
        jButtonPasso = new javax.swing.JButton();
        jButtonPassoAnterior = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtaFita = new javax.swing.JTextArea();
        jMenuBarTitulo = new javax.swing.JMenuBar();
        jMenuArquivo = new javax.swing.JMenu();
        jMenuItemAbrir = new javax.swing.JMenuItem();
        jMenuItemSalvar = new javax.swing.JMenuItem();
        jMenuItemSair = new javax.swing.JMenuItem();
        jMenuEntrada = new javax.swing.JMenu();
        jMenuItemExecucaoRapida = new javax.swing.JMenuItem();
        jMenuItemExecucaoPassoAPasso = new javax.swing.JMenuItem();
        jMenuItemEntradasMultiplas = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemMoverEstado = new javax.swing.JMenuItem();
        jMenuItemNovoEstado = new javax.swing.JMenuItem();
        jMenuItemRemoverEstado = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemNovaAresta = new javax.swing.JMenuItem();
        jMenuItemAlterarAresta = new javax.swing.JMenuItem();
        jMenuItemRemoverAresta = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Máquina de Turing");

        jSPAutomato.setToolTipText("");
        jSPAutomato.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                jSPAutomatoAncestorMoved(evt);
            }
        });
        jSPAutomato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSPAutomatoFocusGained(evt);
            }
        });

        jButtonPasso.setText("Próximo Passo");
        jButtonPasso.setEnabled(false);
        jButtonPasso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPassoActionPerformed(evt);
            }
        });

        jButtonPassoAnterior.setText("Passo Anterior");
        jButtonPassoAnterior.setEnabled(false);
        jButtonPassoAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPassoAnteriorActionPerformed(evt);
            }
        });

        jLabel1.setText("Fita da  Máquina ");

        jtaFita.setEditable(false);
        jtaFita.setColumns(20);
        jtaFita.setRows(5);
        jtaFita.setDisabledTextColor(new java.awt.Color(0, 0, 51));
        jtaFita.setDoubleBuffered(true);
        jScrollPane5.setViewportView(jtaFita);

        jMenuArquivo.setText("Arquivo");

        jMenuItemAbrir.setText("Abrir");
        jMenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemAbrir);

        jMenuItemSalvar.setText("Salvar");
        jMenuItemSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalvarActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemSalvar);

        jMenuItemSair.setText("Sair");
        jMenuItemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSairActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemSair);

        jMenuBarTitulo.add(jMenuArquivo);

        jMenuEntrada.setText("Entrada");

        jMenuItemExecucaoRapida.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExecucaoRapida.setText("Execução Rápida");
        jMenuItemExecucaoRapida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExecucaoRapidaActionPerformed(evt);
            }
        });
        jMenuEntrada.add(jMenuItemExecucaoRapida);

        jMenuItemExecucaoPassoAPasso.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExecucaoPassoAPasso.setText("Passo a Passo");
        jMenuItemExecucaoPassoAPasso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExecucaoPassoAPassoActionPerformed(evt);
            }
        });
        jMenuEntrada.add(jMenuItemExecucaoPassoAPasso);

        jMenuItemEntradasMultiplas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemEntradasMultiplas.setText("Multiplas Entradas");
        jMenuItemEntradasMultiplas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEntradasMultiplasActionPerformed(evt);
            }
        });
        jMenuEntrada.add(jMenuItemEntradasMultiplas);

        jMenuBarTitulo.add(jMenuEntrada);

        jMenu2.setText("Estado");

        jMenuItemMoverEstado.setText("Mover ");
        jMenuItemMoverEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMoverEstadoActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemMoverEstado);

        jMenuItemNovoEstado.setText("Novo");
        jMenuItemNovoEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovoEstadoActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemNovoEstado);

        jMenuItemRemoverEstado.setText("Remover");
        jMenuItemRemoverEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoverEstadoActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemRemoverEstado);

        jMenuBarTitulo.add(jMenu2);

        jMenu3.setText("Aresta");

        jMenuItemNovaAresta.setText("Nova");
        jMenuItemNovaAresta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovaArestaActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemNovaAresta);

        jMenuItemAlterarAresta.setText("Alterar");
        jMenuItemAlterarAresta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAlterarArestaActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemAlterarAresta);

        jMenuItemRemoverAresta.setText("Remover");
        jMenuItemRemoverAresta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoverArestaActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemRemoverAresta);

        jMenuBarTitulo.add(jMenu3);

        setJMenuBar(jMenuBarTitulo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addComponent(jSPAutomato)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonPasso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(55, 55, 55)
                .addComponent(jButtonPassoAnterior)
                .addContainerGap(209, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonPassoAnterior)
                    .addComponent(jButtonPasso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSPAutomato, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExecucaoPassoAPassoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExecucaoPassoAPassoActionPerformed
        // TODO add your handling code here:
        this.jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
        JOptionPane.showMessageDialog(null, "Execute o passo a passo apertando os botões próximo passo e passo anterior","Máquina Turing Passo a Passo", JOptionPane.INFORMATION_MESSAGE);
        jtaFita.setEnabled(false); 
        
        this.jButtonPasso.setEnabled(true);
        int aux = graph.getListaVertexVisitados().size();
        if (aux != 0){
            graph.grafo.setNovaListaVertexId();
        }
        
        boolean b = this.maquinaTuring.executarTuring(graph, false);
        fitaAuxiliar = new String (this.maquinaTuring.getFitaTuringAux());
        String a = this.getStringNaoVazia();
        this.jtaFita.setText(a);
        
        if (b) this.corPassoAPasso = Color.GREEN; //cadeia foi aceita
        else this.corPassoAPasso = Color.RED; //cadeia de entrada rejeitada
        
        
        //passos();
    }//GEN-LAST:event_jMenuItemExecucaoPassoAPassoActionPerformed

    private void jMenuItemEntradasMultiplasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEntradasMultiplasActionPerformed
        // TODO add your handling code here:
        if (graph == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se criar um Autômato Finito!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (graph.grafo.encontraInicial() == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se ter um Estado Inicial!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (graph.grafo.encontraFinal() == null){
            JOptionPane.showMessageDialog(jScrollPane1, "Deve-se ter um Estado Final!", "ERRO", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                MultiplasEntradas mE = new MultiplasEntradas(graph);
                mE.setLocationRelativeTo(null);
                mE.setVisible(true);
                mE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
    }//GEN-LAST:event_jMenuItemEntradasMultiplasActionPerformed

    private void jMenuItemExecucaoRapidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExecucaoRapidaActionPerformed
        // TODO add your handling code here:
        //this.executarAutomatoFinito();
        this.maquinaTuring.executarTuring(graph, false);
        String s = this.maquinaTuring.fitaTuring();
        jtaFita.setEnabled(false);
        this.jtaFita.setText(s);
        jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
    }//GEN-LAST:event_jMenuItemExecucaoRapidaActionPerformed

    private void jMenuItemSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSairActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jMenuItemSairActionPerformed
    
    private void jMenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirActionPerformed
        // TODO add your handling code here:
        if(graph == null) graph = new Graph();
        int k = -1; //não há automato criado
        Object[] options = { "Confirmar", "Cancelar" };
        if (graph.getVertex().size()!= 0)
         k = JOptionPane.showOptionDialog(null, "Você perderá o automato atual, deseja continuar", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
            if (k == 0){
                this.graph.delAllVertex();
                view.cleanImage();
                view.repaint();
            }
            JFileChooser abrirImagem = new JFileChooser();
                //abrirImagem.setCurrentDirectory(new java.io.File("C:\\Users\\Admin\\Documents\\9 semestre\\Teoria\\exercicios jflap\\10.04\\testeMore.jff"));
            
            if (abrirImagem.showOpenDialog(jMenuArquivo) == JFileChooser.APPROVE_OPTION){
            File F = abrirImagem.getSelectedFile();
            this.caminho = F.getAbsolutePath();
            abrirImagem.setCurrentDirectory(new java.io.File(caminho));
            try {
                System.out.println(caminho);
                Arquivo.lerArquivos(caminho, this.graph, this.view);
            } catch (IOException ex) {
                Logger.getLogger(IUMTuring.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
        
       
    }//GEN-LAST:event_jMenuItemAbrirActionPerformed

    private void jSPAutomatoAncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSPAutomatoAncestorMoved
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jSPAutomatoAncestorMoved

    private void jSPAutomatoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSPAutomatoFocusGained
        // TODO add your handling code here:
       
     
    }//GEN-LAST:event_jSPAutomatoFocusGained

    private void jButtonPassoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPassoActionPerformed
        // TODO add your handling code here:
        Vertex v, v2;
       
        int totalPassos = this.graph.getListaVertexVisitados().size();
        int anterior;
        this.qntEstadosVisitados++;
        if (this.qntEstadosVisitados < totalPassos){
            //ainda há estados para serem pintados 
            v = this.graph.getListaVertexVisitados().get(this.qntEstadosVisitados);//pega o próximo estado atual
           
            if (!v.isInicial()){
               anterior = this.qntEstadosVisitados-1;
               v2 = this.graph.getListaVertexVisitados().get(anterior);//repinta o estado anterior
               v2.setColor(Color.YELLOW);
               view.cleanImage();
               view.repaint();
               this.jButtonPassoAnterior.setEnabled(true);//habilita o botão anterior
            }else this.jButtonPassoAnterior.setEnabled(false);//desabilita o botão anterior
           
            v.setColor(this.corPassoAPasso);//pinta o estado
            this.imprimeFitaNaoVazia(true);
            view.cleanImage();
            view.repaint();
            if (v.isFinal()){
               //ja acabou a lista de nós visitados
                this.qntEstadosVisitados = -1;
                this.jButtonPasso.setEnabled(false); 
                this.jButtonPassoAnterior.setEnabled(false);
                JOptionPane.showMessageDialog(null, "Acabou o passo a passo","Máquina Turing", JOptionPane.INFORMATION_MESSAGE);
                v.setColor(Color.YELLOW);
                view.cleanImage();
                view.repaint();
            }
        }else {
            //ja acabou a lista de nós visitados
            this.jButtonPasso.setEnabled(false); 
            this.jButtonPassoAnterior.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Acabou o passo a passo","Máquina Turing", JOptionPane.INFORMATION_MESSAGE);
            //repinta o ultimo estado
            this.qntEstadosVisitados--;
            v = this.graph.getListaVertexVisitados().get(this.qntEstadosVisitados);//pega o próximo estado atual
            v.setColor(Color.YELLOW);
            view.cleanImage();
            view.repaint();
            this.qntEstadosVisitados = -1;
        }
    }//GEN-LAST:event_jButtonPassoActionPerformed

    private void jButtonPassoAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPassoAnteriorActionPerformed
        // TODO add your handling code here:
        Vertex atual, anterior;
        atual = this.graph.getListaVertexVisitados().get(this.qntEstadosVisitados);//pega o próximo estado atual
        int anteriorAux;
        atual.setColor(Color.yellow);
        view.cleanImage();
        view.repaint();
        this.qntEstadosVisitados--;
        anterior = this.graph.getListaVertexVisitados().get(this.qntEstadosVisitados);//pega o próximo estado atual
        anterior.setColor(this.corPassoAPasso);
        this.imprimeFitaNaoVazia(false);
        view.cleanImage();
        view.repaint();
        if (anterior.isInicial()) this.jButtonPassoAnterior.setEnabled(false);
        
            
    }//GEN-LAST:event_jButtonPassoAnteriorActionPerformed

    private void jMenuItemAlterarArestaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAlterarArestaActionPerformed
        // TODO add your handling code here:
         jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
        this.jMenuItemAlterarAresta.setSelected(true);
    }//GEN-LAST:event_jMenuItemAlterarArestaActionPerformed

    private void jMenuItemMoverEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMoverEstadoActionPerformed
        // TODO add your handling code here:
        this.jMenuItemMoverEstado.setSelected(true);
        this.jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemAlterarAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        
       
    }//GEN-LAST:event_jMenuItemMoverEstadoActionPerformed

    private void jMenuItemRemoverArestaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoverArestaActionPerformed
        // TODO add your handling code here:
        jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(true);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
        this.jMenuItemAlterarAresta.setSelected(false);
    }//GEN-LAST:event_jMenuItemRemoverArestaActionPerformed

    private void jMenuItemNovoEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovoEstadoActionPerformed
        // TODO add your handling code here:
        jMenuItemNovoEstado.setSelected(true);
        this.jMenuItemAlterarAresta.setSelected(false);
        this.jMenuItemAlterarAresta.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
    }//GEN-LAST:event_jMenuItemNovoEstadoActionPerformed

    private void jMenuItemRemoverEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoverEstadoActionPerformed
        // TODO add your handling code here:
         jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(true);
        this.jMenuItemMoverEstado.setSelected(false);
    }//GEN-LAST:event_jMenuItemRemoverEstadoActionPerformed

    private void jMenuItemNovaArestaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovaArestaActionPerformed
        // TODO add your handling code here:
        jMenuItemNovoEstado.setSelected(false);
        this.jMenuItemNovaAresta.setSelected(true);
        this.jMenuItemAlterarAresta.setSelected(false);
        this.jMenuItemRemoverAresta.setSelected(false);
        this.jMenuItemRemoverEstado.setSelected(false);
        this.jMenuItemMoverEstado.setSelected(false);
        this.jMenuItemAlterarAresta.setSelected(false);
    }//GEN-LAST:event_jMenuItemNovaArestaActionPerformed

    private void jMenuItemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalvarActionPerformed
        if (graph != null){
            try {
                // TODO add your handling code here:
                String filename = File.separator+"jff";
                JFileChooser fc = new JFileChooser(new File(filename));
                // Mostra a dialog de save file
                fc.showSaveDialog(this);
                File selFile = fc.getSelectedFile();
                Arquivo.salvarArquivo(selFile.getAbsolutePath(), graph);
            } catch (SAXException ex) {
                Logger.getLogger(IUMTuring.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(IUMTuring.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(IUMTuring.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(IUMTuring.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItemSalvarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                IUMTuring janela = new IUMTuring();
                janela.setLocationRelativeTo(null);
                janela.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPasso;
    private javax.swing.JButton jButtonPassoAnterior;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenuArquivo;
    private javax.swing.JMenuBar jMenuBarTitulo;
    private javax.swing.JMenu jMenuEntrada;
    private javax.swing.JMenuItem jMenuItemAbrir;
    private javax.swing.JMenuItem jMenuItemAlterarAresta;
    private javax.swing.JMenuItem jMenuItemEntradasMultiplas;
    private javax.swing.JMenuItem jMenuItemExecucaoPassoAPasso;
    private javax.swing.JMenuItem jMenuItemExecucaoRapida;
    private javax.swing.JMenuItem jMenuItemMoverEstado;
    private javax.swing.JMenuItem jMenuItemNovaAresta;
    private javax.swing.JMenuItem jMenuItemNovoEstado;
    private javax.swing.JMenuItem jMenuItemRemoverAresta;
    private javax.swing.JMenuItem jMenuItemRemoverEstado;
    private javax.swing.JMenuItem jMenuItemSair;
    private javax.swing.JMenuItem jMenuItemSalvar;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JScrollPane jSPAutomato;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jtaFita;
    // End of variables declaration//GEN-END:variables
}
