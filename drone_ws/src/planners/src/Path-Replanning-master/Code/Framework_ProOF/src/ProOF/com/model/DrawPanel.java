/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class DrawPanel extends DrawSpace {

    public final Cursor CursorHand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public final Cursor CursorDefault = Cursor.getDefaultCursor();
    public Model project;
    public LinkedList<dVertex> selected = new LinkedList<dVertex>();
    private dVertex Mov = null;
    private CallBack callBackSave;

    private static Font fontRuler = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    public DrawPanel(CallBack call) {
        this.callBackSave = call;
        this.setBackground(Color.lightGray);
    }

    public void setProject(Model project) {
        synchronized(this){
            this.project = project;
            repaint();
        }
    }

    @Override
    protected void paintComponentAntes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width() - 1, height() - 1);
        paintRuler(g2);
        
        final int r = 3;
        
        g2.setColor(Color.BLACK);
        if((dVertex.paint_lines/4)%2==0){
            g2.drawLine(5, 10, 35, 10);
            g2.fillOval(5-r, 10-r, 2*r, 2*r);
            g2.fillOval(35-r, 10-r, 2*r, 2*r);
        }
        g2.setColor(Color.BLUE);
        if((dVertex.paint_lines/2)%2==0){
            g2.drawLine(5, 20, 35, 20);
            g2.fillOval(5-r, 20-r, 2*r, 2*r);
            g2.fillOval(35-r, 20-r, 2*r, 2*r);
        }
        g2.setColor(Color.RED);
        if((dVertex.paint_lines/1)%2==0){
            g2.drawLine(5, 30, 35, 30);
            g2.fillOval(5-r, 30-r, 2*r, 2*r);
            g2.fillOval(35-r, 30-r, 2*r, 2*r);
        }
    }

    private void paintRuler(Graphics2D g2) {
        g2.setFont(fontRuler);
        g2.setColor(Color.gray);
        g2.fillRect(0, 0, width(), 40);
        g2.fillRect(0, 0, 40, height());
        g2.setColor(Color.black);

        drawScaleNumeric(g2, width(), Cx(), true);
        drawScaleNumeric(g2, height(), Cy(), false);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, 40, 40);
    }
    private void drawScaleNumeric(Graphics2D g2, int dimension, double center, boolean isX) {
        double dim1 = center - dimension * (zoom()) / 2;
        double dim2 = center + dimension * (zoom()) / 2;
        double delta = (dim2 - dim1) * 100 / dimension;
        double incr = (int) Math.log10(delta);
        incr = delta > Math.pow(10, incr + 1) / 2.0 ? incr + 1 : incr;
        incr = (int) Math.pow(10.0, incr);
        incr = incr / 100;
        delta = delta / incr;

        int i = 0;
        while ((int) (dimension / 2 - center * zoom() + i * delta) > 40) {
            i--;
        }
        while ((int) (dimension / 2 - center * zoom() + i * delta) < 40) {
            i++;
        }
        while ((int) (dimension / 2 - center * zoom() + i * delta) < dimension) {
            g2.setColor(Color.BLACK);
            if (isX) {
                g2.drawLine((int) (dimension / 2 - center * zoom() + i * delta), 40 - 1,
                        (int) (dimension / 2 - center * zoom() + i * delta), 30 - 1);
                g2.setColor(Color.GRAY);
                g2.drawLine((int) (dimension / 2 - center * zoom() + i * delta) + 1, 40 - 1,
                        (int) (dimension / 2 - center * zoom() + i * delta) + 1, 30 - 1);
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("%d", (int) (i * 100 / incr)),
                        (int) (dimension / 2 - center * zoom() + i * delta), 20);

            } else {
                g2.drawLine(30 - 1, (int) (dimension / 2 - center * zoom() + i * delta),
                        40 - 1, (int) (dimension / 2 - center * zoom() + i * delta));
                g2.setColor(Color.GRAY);
                g2.drawLine(30 - 1, (int) (dimension / 2 - center * zoom() + i * delta) + 1,
                        40 - 1, (int) (dimension / 2 - center * zoom() + i * delta) + 1);
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("%d", (int) (i * 100 / incr)), 5,
                        (int) (dimension / 2 - center * zoom() + i * delta));
            }
            i++;
        }
    }

    @Override
    protected void paintComponentDepois(Graphics2D g2) {
        super.paintComponentDepois(g2);
        synchronized(this){
            if (project == null) {
                return;
            }
            for (dVertex gr : project.vertexes) {
                gr.paint_vertex(g2);
            }
            for (dVertex gr : selected) {
                gr.paint_vertex(g2);
            }
            if(Mov!=null){
                Mov.paint_vertex(g2);
            }
            for (dVertex gr : project.vertexes) {
                gr.paint_lines(g2);
            }
        }
    }

    @Override
    protected void MouseClicked(MouseEvent e, double X, double Y) {
        if (project == null) {
            return;
        }

        
        boolean cursor = false;
        for (dVertex gr : project.vertexes) {
            gr.setBackground(getBackground());
            if (gr.contain(X, Y)) {
                cursor = true;
                if (selected.contains(gr)) {
                    selected.remove(gr);
                } else {
                    selected.addLast(gr);
                }
            }
        }
        if (!e.isControlDown()) {
            while(selected.size()>1){
                selected.removeFirst();
            }
            //selected.clear();
        }
        
        
        if (cursor) {
            setCursor(CursorHand);
        } else {
            selected.clear();
            setCursor(CursorDefault);
        }
        for (dVertex gr : selected) {
            gr.setSelected(Color.RED);
        }
    }

    @Override
    protected void MouseMoved(MouseEvent e, double X, double Y) {
        if (project == null) {
            return;
        }
        boolean cursor = false;
        for (dVertex gr : project.vertexes) {
            if (gr.contain(X, Y)) {
                gr.setMoved(Color.MAGENTA);
                cursor = true;
            } else {
                gr.setBackground(getBackground());
            }
        }
        if (cursor) {
            setCursor(CursorHand);
        } else {
            setCursor(CursorDefault);
        }
        for (dVertex gr : selected) {
            gr.setSelected(Color.RED);
        }
    }
    public static final int Grid = 10;

    @Override
    protected void MousePressed(MouseEvent e, double X, double Y) {
        if (project == null) {
            return;
        }
        boolean cursor = false;
        for (dVertex gr : project.vertexes) {
            if (gr.contain(X, Y)) {
                Mov = gr;
                Mov.mark(X, Y, Grid);
                cursor = true;
            }
        }
        if (cursor) {
            for (dVertex gr : selected) {
                gr.mark(X, Y, Grid);
            }
        }
    }

    @Override
    protected void MouseDragged(MouseEvent e, double X, double Y) {
        if (project == null) {
            return;
        }
        if (Mov != null) {
            Mov.mov(X, Y, Grid);
            for (dVertex gr : selected) {
                gr.mov(X, Y, Grid);
            }
        }
    }

    @Override
    protected void MouseReleased(MouseEvent e, double X, double Y) {
        if (project == null) {
            return;
        }
        if (Mov != null) {
            /*if(e.isControlDown()){
            Mov.mov((int)(X / 1000)*1000,(int)(Y / 1000)*1000);
            for(Graph gr : selected){
            gr.mov((int)(X / 1000)*1000,(int)(Y / 1000)*1000);
            }
            }*/
            
            Mov = null;
            callBackSave.main();
        }
    }
}
