/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import static java.awt.RenderingHints.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
/**
 *
 * @author Marcio
 */
public abstract class DrawSpace extends JPanel {

    private final double taxaZoon = 1.125;

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

    public DrawSpace() {
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

            //g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        //g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_SPEED);
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

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
    protected double getXReal(double X) {
        return this.Cx + (X - width / 2) / zoom;
    }

    /**
     * @return retorna um ponto Y com as cordenadas reais, de onde se clicou apos ter calculado o zoon
     */
    protected double getYReal(double Y) {
        return this.Cy + (Y - height / 2) / zoom;
    }

    protected void setCentro(double Cx, double Cy) {
        this.Cx = Cx;
        this.Cy = Cy;
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
    }

    protected void ReiniciarSistema() {
        this.Cx = 0;
        this.Cy = 0;
        dx = 0;
        dy = 0;
        zoom = 1;
        Xt = width / 2 - Cx * zoom;
        Yt = height / 2 - Cy * zoom;
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
        //g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        //g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_SPEED);
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        g2.translate(Xt, Yt);
        g2.scale(zoom, zoom);

        paintComponentDepois(g2);

        g2.scale(1.0 / zoom, 1.0 / zoom);
        g2.translate(-Xt, -Yt);

        paintComponentAntes(g2);
    }

    //------------------------------------------metodos abstratos-----------------------------
    /**
     * @param e MouseEvent
     * @param X Posição X real, de onde se clicou, considerando o zoon
     * @param Y Posição Y real, de onde se clicou, considerando o zoon
     */
    protected void MouseClicked(MouseEvent e, double X, double Y) {
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



    /**Pinta no Graphics antes da mudança de cordenadas devido a translação e o zoon
     * que será aplicado.
     * @param g2
     */
    protected void paintComponentAntes(Graphics2D g2) {
    }

    /**Pinta no Graphics depois da mudança de cordenadas devido a translação e o zoon
     * que será aplicado.(apos)<Br>
     * g2.translate(width/2-Cx*zoon, height/2-Cy*zoon);<br>
     * g2.scale(zoon, zoon);<br>
     * @param g2
     */
    protected void paintComponentDepois(Graphics2D g2) {
    }
}
