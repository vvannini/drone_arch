/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import static java.awt.RenderingHints.*;
import java.awt.event.MouseAdapter;
import java.util.Locale;
import javax.swing.JFrame;
/**
 *
 * @author Marcio
 */
public abstract class sPanelDraw extends sPanel {

    public final double taxaZoon = 1.125;
    public final int offsetW = 60;
    public final int offsetH = 50;
    
    private double zoom = 1.0;
    private double Cx = 0;
    private double Cy = 0;
    private double dx = 0;
    private double dy = 0;
    private double Xt;
    private double Yt;
    private boolean translated = false;

    private int width;
    private int height;

    private boolean anti_aliasing = true;
    private float composite = 1.0f;
    
    private final int UNIT = 100;
    public final int unit(double v){
        return (int)(v*UNIT);
    }
    public final double inverse(double v){
        return v/UNIT;
    }
    
    public sPanelDraw(Color default_color) {
        super(default_color);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                MouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                MousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseReleased(e);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                MouseMoved(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                MouseDragged(e);
            }
        });
        this.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                MouseWheelMoved(e);
            }
        });

        Init();
    }

    @Override
    public final void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        this.width = preferredSize.width;
        this.height = preferredSize.height;
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
        changeSize();
        repaint();
    }

    private static Font fontRuler = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    public void paintRuler(Graphics2DReal gr, boolean grid) {
        paintRuler(gr.g2, grid);
    }
    public void paintRuler(Graphics2D g2, boolean grid) {
        Font save = g2.getFont();
        
        g2.setFont(fontRuler);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, width(), offsetH);
        g2.fillRect(0, 0, offsetW, height());
        g2.setColor(Color.black);
        g2.drawRect(0, 0, width(), offsetH);
        g2.drawRect(0, 0, offsetW, height());
        
        drawScaleNumeric(g2, width(), Cx(), true, grid);
        drawScaleNumeric(g2, height(), Cy(), false, grid);

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, offsetW, offsetH);
        g2.setColor(Color.black);
        g2.drawRect(0, 0, offsetW, offsetH);
        
        g2.setFont(save);
    }
    
    private int calculate(int i, int dimension, double center, double distance){
        return (int) (dimension / 2 - center * zoom() + i * distance); 
    }
    private void drawScaleNumeric(Graphics2D g2, int dimension, double center, boolean isX, boolean grid) {

        //double incr = (int)(Math.log10(zoom()*UNIT));
        //incr = zoom()*UNIT > Math.pow(10, incr + 1) / 2.0 ? incr + 1 : incr;
        //g2.drawString("incr = "+incr, 100, 100);
        //incr = incr>99 ? incr+1 : incr>-99 ? incr : incr-1;
        //incr = Math.pow(10.0, incr);
        //incr = incr / UNIT;
        
        final int CENT = 100; 
        
        double incr = 1e+9;
        double distance = zoom()*CENT / incr;
        while(distance<offsetW){
            incr /= 10.0;
            distance = zoom()*CENT / incr;
        }

        int i = 0;
        while (calculate(i, dimension, center, distance) > offsetW) {
            i--;
        }
        while (calculate(i, dimension, center, distance) < offsetW) {
            i++;
        }
        final double ref =  1/ (zoom()*2);
        
        while ((int) (dimension / 2 - center * zoom() + i * distance) < dimension) {
            g2.setColor(Color.BLACK);
            if (isX) {
                g2.setColor(Color.BLACK);
                g2.drawLine(calculate(i, dimension, center, distance), offsetH - 1,
                        calculate(i, dimension, center, distance), offsetH-10 - 1);
                g2.setColor(Color.GRAY);
                g2.drawLine(calculate(i, dimension, center, distance) + 1, offsetH - 1,
                        calculate(i, dimension, center, distance) + 1, offsetH-10 - 1);
                
                if(grid){
                    g2.setColor(Color.GRAY);
                    g2.drawLine(calculate(i, dimension, center, distance), offsetH,
                            calculate(i, dimension, center, distance), height);
                }
                    
                
                g2.setColor(Color.BLACK);
                double val = (i / incr);
                int x = (int) (dimension / 2 - center * zoom() + i * distance);
                int y = 20;
                if(Math.abs(ref)>1e4){
                    g2.drawString(String.format("%1.0e",  val), x, y);
                }else if(Math.abs(ref)>0.1){
                    g2.drawString(String.format("%d",  (int)val), x, y);
                }else if(Math.abs(ref)>0.01){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.1f",  val), x, y);
                }else if(Math.abs(ref)>0.001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.2f",  val), x, y);
                }else if(Math.abs(ref)>0.0001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.3f",  val), x, y);
                }else if(Math.abs(ref)>0.00001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.4f",  val), x, y);
                }else if(Math.abs(ref)>0.000001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.5f",  val), x, y);
                }else if(Math.abs(ref)>0.0000001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.6f",  val), x, y);
                }else{
                    g2.drawString(String.format("%g",  val), x, y);
                }
            } else {
                g2.setColor(Color.BLACK);
                g2.drawLine(offsetW-10 - 1, calculate(i, dimension, center, distance),
                        offsetW - 1, calculate(i, dimension, center, distance));
                g2.setColor(Color.GRAY);
                g2.drawLine(offsetW-10 - 1, calculate(i, dimension, center, distance) + 1,
                        offsetW - 1, calculate(i, dimension, center, distance) + 1);
                
                if(grid){
                    g2.setColor(Color.GRAY);
                    g2.drawLine(offsetW, calculate(i, dimension, center, distance),
                        width, calculate(i, dimension, center, distance));
                    
                }
                g2.setColor(Color.BLACK);
                
                
                double val = (i / incr);
                int x = 5;
                int y = (int) (dimension / 2 - center * zoom() + i * distance)-5;
                
                if(Math.abs(ref)>1e4){
                    g2.drawString(String.format("%1.0e",  val), x, y);
                }else if(Math.abs(ref)>0.1){
                    g2.drawString(String.format("%d",  (int)val), x, y);
                }else if(Math.abs(ref)>0.01){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.1f",  val), x, y);
                }else if(Math.abs(ref)>0.001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.2f",  val), x, y);
                }else if(Math.abs(ref)>0.0001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.3f",  val), x, y);
                }else if(Math.abs(ref)>0.00001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.4f",  val), x, y);
                }else if(Math.abs(ref)>0.000001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.5f",  val), x, y);
                }else if(Math.abs(ref)>0.0000001){
                    g2.drawString(String.format(Locale.ENGLISH,"%1.6f",  val), x, y);
                }else{
                    g2.drawString(String.format("%g",  val), x, y);
                }
                
            }
            i++;
        }
    }
    
    public void changeSize(){
        
    }
    
    public int width() {
        return width;
    }
    public int height() {
        return height;
    }
    public double zoom(){
        return zoom;
    }
    public double Cx(){
        return Cx;
    }
    public double Cy(){
        return Cy;
    }

    public void setAntiAliasing(boolean flag){
        anti_aliasing = flag;
    }
    public void setComposite(float composite){
        this.composite = composite;
    }

    protected final void Init() {
    }
    //------------------------------------------metodos privates--------------------------------

    private void MouseClicked(MouseEvent e) {
        this.MouseClicked(e, e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.MouseClicked(e, getXReal(e.getX()), getYReal(e.getY()));
        } else {
            this.Cx = getXReal(e.getX());
            this.Cy = getYReal(e.getY());
            Xt = width / 2 - Cx * zoom;
            Yt = height / 2 - Cy * zoom;
        }
        repaint();
    }

    private void MouseMoved(MouseEvent e) {
        if (width != getWidth() || height != getHeight()) {
            width = getWidth();
            height = getHeight();
        }
        this.MouseMoved(e, getXReal(e.getX()), getYReal(e.getY()));
        repaint();
    }

    private void MouseWheelMoved(MouseWheelEvent e) {
        zoom = zoom * (e.getWheelRotation() < 0 ? taxaZoon : 1.0 / taxaZoon);
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
        this.MouseWheelMoved(e, getXReal(e.getX()), getYReal(e.getY()));
        repaint();
    }
    

    private void MousePressed(MouseEvent e) {
        //this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        //System.out.println(e.getButton() + " , " + MouseEvent.BUTTON3);
        if (e.getButton() == MouseEvent.BUTTON3) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            this.dx = getXReal(e.getX());
            this.dy = getYReal(e.getY());
            translated = true;
        }
        MousePressed(e, getXReal(e.getX()), getYReal(e.getY()));
        repaint();
    }

    private void MouseReleased(MouseEvent e) {
        translated = false;
        this.setCursor(Cursor.getDefaultCursor());
        MouseReleased(e, getXReal(e.getX()), getYReal(e.getY()));
        repaint();
    }

    private void MouseDragged(MouseEvent e) {
        if (translated) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            this.Cx += dx - getXReal(e.getX());
            this.Cy += dy - getYReal(e.getY());
            this.dx = getXReal(e.getX());
            this.dy = getYReal(e.getY());

            Xt = width / 2 - Cx * zoom;
            Yt = height / 2 - Cy * zoom;
            
        }else{
            MouseDragged(e, getXReal(e.getX()), getYReal(e.getY()));
        }
        repaint();
    }

    //------------------------------------------metodos publics e protecteds-------------------------------
    /**
     * @return retorna um ponto X com as cordenadas reais, de onde se clicou apos ter calculado o zoon
     */
    private double getXReal(double X) {
        return this.Cx + (X - width / 2) / zoom;
    }

    /**
     * @return retorna um ponto Y com as cordenadas reais, de onde se clicou apos ter calculado o zoon
     */
    private double getYReal(double Y) {
        return this.Cy + (Y - height / 2) / zoom;
    }
    
//    /**
//     * @return retorna um ponto X com as cordenadas reais, de onde se clicou apos ter calculado o zoon
//     */
//    protected double getXUnit(double x){
//        return inverse(getXReal(x));
//    }
//    /**
//     * @return retorna um ponto Y com as cordenadas reais, de onde se clicou apos ter calculado o zoon
//     */
//    protected double getYUnit(double y){
//        return inverse(getYReal(y));
//    }
    
    protected double getXOrg(double X){
        return (X-Cx)*zoom + width/2;
    }
    protected double getYOrg(double Y){
        return (Y-Cy)*zoom + height/2;
    }

    public void goTo(double x, double y, double w, double h){
        //ReiniciarSistema();
        goTo(x+w/2-inverse(offsetW), y+h/2-inverse(offsetH));
        goZoom(Math.min(inverse(width-offsetW)/w, inverse(height-offsetH)/h));
        repaint();
    }
    public void goZoom(double zoom){
        this.zoom = zoom;
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
        repaint();
    }
    public void goTo(double Cx, double Cy) {
        this.Cx = unit(Cx);
        this.Cy = unit(Cy);
        Xt = width / 2 - this.Cx * zoom;
        Yt = height / 2 - this.Cy * zoom;
        repaint();
    }
    public void goMove(double dx, double dy) {
        this.Cx = Cx+unit(dx);
        this.Cy = Cy+unit(dy);
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
        repaint();
    }
    public void restart_system() {
        this.Cx = 0;
        this.Cy = 0;
        dx = 0;
        dy = 0;
        zoom = 1;
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
        repaint();
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        if(anti_aliasing){
            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        }
        if(composite<1.0f){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, composite));
        }
        
        Graphics2DReal gr = new Graphics2DReal(g2, UNIT);
        //g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        //g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_SPEED);
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        //g2.scale(1, -1);
        try {
            paintStart(gr);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        g2.translate(Xt, Yt);
        g2.scale(zoom, zoom);

        try {
            paintMiddle(gr);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        

        g2.scale(1.0 / zoom, 1.0 / zoom);
        g2.translate(-Xt, -Yt);
        
        try {
            paintEnd(gr);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
    }

    //------------------------------------------metodos abstratos-----------------------------
    /**
     * @param e MouseEvent
     * @param X Posição X real, de onde se clicou, considerando o zoon
     * @param Y Posição Y real, de onde se clicou, considerando o zoon
     */
    protected void MouseClicked(MouseEvent e, double X, double Y) {
    }
    protected void MouseClicked(MouseEvent e, int X, int Y) {
    }
    /**
     * @param e MouseEvent
     * @param X Posição X real, de onde se clicou, considerando o zoon
     * @param Y Posição Y real, de onde se clicou, considerando o zoon
     */
    protected void MouseMoved(MouseEvent e, double X, double Y) {
    }
    

    /**
     * @param e MouseEvent
     * @param X Posição X real, de onde se clicou, considerando o zoon
     * @param Y Posição Y real, de onde se clicou, considerando o zoon
     */
    protected void MouseWheelMoved(MouseWheelEvent e, double X, double Y) {
    }

    protected void MousePressed(MouseEvent e, double X, double Y) {
    }

    protected void MouseReleased(MouseEvent e, double X, double Y) {
    }
    protected void MouseDragged(MouseEvent e, double X, double Y) {
    }


    protected void paintStart(Graphics2DReal gr) throws Throwable{
    }
    /**Pinta no Graphics depois da mudança de cordenadas devido a translação e o zoon
     * que será aplicado.(apos)<Br>
     * g2.translate(width/2-Cx*zoon, height/2-Cy*zoon);<br>
     * g2.scale(zoon, zoon);<br>
     * @param gr
     * @throws java.lang.Throwable
     */
    protected void paintMiddle(Graphics2DReal gr) throws Throwable{
    }
    
    /**Pinta no Graphics antes da mudança de cordenadas devido a translação e o zoon
     * que será aplicado.
     * @param gr
     * @throws java.lang.Throwable
     */
    protected void paintEnd(Graphics2DReal gr) throws Throwable{
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String args[]){
        JFrame frame = new JFrame("draw");
        
        sPanelDraw draw = new sPanelDraw(Color.WHITE) {
            @Override
            protected void paintEnd(Graphics2DReal gr) {
                gr.g2.drawRect(0, 0, width()-1, height()-1);
                
                /*
                g2.setColor(Color.red);
                g2.drawRect(width()/2-45, height()/2-45, 90, 90);
                
                g2.drawOval(width()/2-45, -100+height()/2-45, 90, 90);
                */
                
                //System.out.println(width() + " : "+height());
            }
            @Override
            protected void paintMiddle(Graphics2DReal gr) {
                gr.g2.drawRect(-40, -40, 80, 80);
                
                gr.g2.drawOval(-40, -140, 80, 80);
            }
            protected void MouseClicked(MouseEvent e, int X, int Y) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    goTo(+0, -40, 40, 40);
                }
            }
            
        };
        
        
        frame.add(draw);
        
        frame.setSize(1000, 700);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        draw.Config(900, 600);
        draw.restart_system();
    }
    
}
