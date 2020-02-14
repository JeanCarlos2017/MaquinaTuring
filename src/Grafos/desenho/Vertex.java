package Grafos.desenho;

import java.awt.Color;

public class Vertex {
    private float x;
    private float y;
    private int ray = 20;
    private int id;
    private boolean 
            estadoFinal = false,
            estadoInicial = false,
            selected = false;
    private Color colorSelected = Color.cyan;
    private Color color = Color.yellow;
    private String name = "", label = "";

    public Vertex(int ID, String name) {
        this.id = ID;
        this.name = name;
    }

    public Vertex(float x, float y, int id, String name, boolean isInicial, boolean isFinal) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.name = name;
        this.estadoInicial = isInicial;
        this.estadoFinal = isFinal;
    }
    
    
    
    public Vertex(int ID, String name, float x, float y) {
        this.id = ID;
        this.name = name;
        this.x = x;
        this.y = y;
    }
    
    public Vertex(int ID, String name, String label, float x, float y) {
        this.id = ID;
        this.name = name;
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public void draw(java.awt.Graphics2D g2) {
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        g2.setStroke(new java.awt.BasicStroke(2.0f));

        if (this.ray != 0) {
            if (this.isSelected())
                g2.setColor(this.getColorSelected());
            else
                g2.setColor(this.getColor());
            g2.fillOval(((int) this.x) - this.getRay(), ((int) this.y) - this.getRay(), this.getRay() * 2, this.getRay() * 2);
            g2.setColor(Color.darkGray);
            if (this.isFinal())
                g2.drawOval(((int) this.x) - this.getRay() + 4, ((int) this.y) - this.getRay() + 4, this.getRay() * 2 - 8, this.getRay() * 2 - 8);
            g2.drawOval(((int) this.x) - this.getRay(), ((int) this.y) - this.getRay(), this.getRay() * 2, this.getRay() * 2);
            if (this.isInicial()){
                int [] xPoints = {(int)this.getX()-this.getRay(), (int)this.getX()-this.getRay()*2, (int)this.getX()-this.getRay()*2};
                int [] yPoints = {(int)this.getY(), (int)this.getY()-this.getRay(), (int)this.getY()+this.getRay()};
                g2.drawPolygon(xPoints, yPoints, 3);
            }
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            g2.setStroke(new java.awt.BasicStroke(3.0f));
            g2.setColor(Color.black);
            //g2.drawString(ID, x-ID.length()*3, y+ray/4);
            g2.drawString(name, x-name.length()*3, y+ray/4);
            g2.drawString(label, x-label.length()*3, y-ray-3);
        }
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColorSelected() {
        return colorSelected;
    }

    public void setColorSelected(Color colorSelected) {
        this.colorSelected = colorSelected;
    }

    public boolean isFinal() {
        return estadoFinal;
    }

    public void setFinal(boolean estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public boolean isInicial() {
        return estadoInicial;
    }

    public void setInicial(boolean estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRay() {
        return ray;
    }

    public void setRay(int ray) {
        this.ray = ray;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }
}
