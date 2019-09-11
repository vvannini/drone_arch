/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.pog.problem.CCQSP;

import ProOF.apl.pog.problem.CCQSP.CCQSPMap.Obstacle;
import static ProOF.com.language.Approach.job;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class CCQSPPlot {
    public int width, heigth;//, cx, cy;
    public double zoon;
    
    
    public static final double offset = 0.2;
    
    
    private int dX = 11;
    private String format = "%1.0f";
    private final int format_X = 1;
    private final int format_Y = 90;
    
    
    private static final boolean INVERT_Y = false;//true;
    public static final boolean PLOT_NORM = false;
    public static final boolean PLOT = true;
    public static final boolean TEMP_FIX = false;
    
    
    private JFrame frame;
    private JPanel draw;
    private ArrayList<double[][]> listMU = new ArrayList<double[][]>();
    private ArrayList<double[]> listD = new ArrayList<double[]>();
    private ArrayList<int[][][]> listZ = new ArrayList<int[][][]>();
    
    private final CCQSPMap map;

    public CCQSPPlot(CCQSPMap map) {
        this.map = map;
    }
    
    
    public void clear(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listMU.clear();
            }
        }); 
    }
    private int tt = -1;
    private int t_plot = -1;
    private double obj = -1;
    private double dist = -1;
    private double gap = -1;
    private double time = -1;
    public void plot(final double[][] vMt) {
        plot(vMt, dX, format);
    }
    public void plot(final String title, final double[][] vMt) {
        plot(title, vMt, dX, format);
    }
    public void plot(final String title, final double[][] vMt, final double[] vDt) {
        plot(title, vMt, vDt, dX, format);
    }
    public void plot(final String title, final double[][] vMt, final double[] vDt,
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, vDt, dX, format, obj, dist, gap, time);
    }
    public void plot(final double[][] vMt, final int dX, final String format) {
        plot("title", vMt, dX, format);
    }
    public void plot(String title, final double[][] vMt, final int dX, final String format) {
        plot(title, vMt, null, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[] vDt, final int dX, final String format) {
        plot(title, vMt, vDt, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[] vDt, final int dX, final String format, 
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, vDt, dX, format, null, -1, obj, dist, gap, time);
    }
    public void plot(final String title, final double[][] vMt, final int[][][] Zjti, final int t) {
        plot(title, vMt, null, dX, format, Zjti, t, -1, -1, -1, -1);
    }
    public void plot(
        final String title, final double[][] vMt, final double[] vDt, 
        final int dX, final String format, final int[][][] Zjti, final int t,
        final double obj, final double dist, final double gap, final double time
    ) {
        System.out.println("plot");
        this.dX = dX;
        this.format = format;
        this.obj = obj;
        this.dist = dist;
        this.gap = gap;
        this.time = time;
        if(vMt!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listMU.clear();
                    listMU.add(vMt);
                }
            }); 
        }
        if(vDt!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listD.clear();
                    listD.add(vDt);
                }
            }); 
        }
        if(Zjti!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tt = t;
                    listZ.clear();
                    listZ.add(Zjti);
                }
            }); 
        }else{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tt = t;
                    listZ.clear();
                }
            }); 
        }
        if(PLOT){
            if(frame==null){
                frame = new JFrame();

                draw = new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        pintar_cartesian(g, true);
                    }
                };
                draw.setPreferredSize(new Dimension(width, heigth));
                JButton btm = new JButton("next");
                btm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        t_plot = (t_plot+1)%(map.T+1);
                        draw.repaint();
                    }
                });
                btm.setSize(new Dimension(120, 23));
                frame.setLayout(new FlowLayout());
                frame.add(btm);
                frame.add(draw);
                frame.setSize(width+20, heigth+85);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
            draw.repaint();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setTitle(title);
                }
            }); 
        }
        
            
    }
    public void title(final String title) {
        if(frame!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setTitle(title);
                }
            });
        }
            
    }
    public void save(){
        save("");
    }
    public void save(String name){
        BufferedImage img = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        
        t_plot = -1;
        
        try {
            pintar_cartesian(g, false);
            
            
            //System.out.println("save in ./sol_"+name+".png");
            //ImageIO.write(img,   "png", new File("C:/zPNG/sol_"+Identificador+"_"+name+".png"));
            if(job==null){
                ImageIO.write(img,   "png", new File("./sol_D"+map.DELTA+"_T"+map.T+"_J"+map.J+"_"+map.Identificador+"_"+name+".png"));
            }else{
                ImageIO.write(img,   "png", new File("./sol_D"+map.DELTA+"_T"+map.T+"_J"+map.J+"_"+job.getName()+"_"+map.Identificador+"_"+name+".png"));
            }
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(!TEMP_FIX)JOptionPane.showMessageDialog(frame, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
        try {
            pintar_cartesian(g, true);
            
            //System.out.println("save in ./sol_"+name+".png");
            //ImageIO.write(img,   "png", new File("C:/zPNG/sol_"+Identificador+"_"+name+".png"));
            if(job==null){
                ImageIO.write(img,   "png", new File("./log_D"+map.DELTA+"_T"+map.T+"_J"+map.J+"_"+map.Identificador+"_"+name+".png"));
            }else{
                ImageIO.write(img,   "png", new File("./log_D"+map.DELTA+"_T"+map.T+"_J"+map.J+"_"+job.getName()+"_"+map.Identificador+"_"+name+".png"));
            }
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(!TEMP_FIX)JOptionPane.showMessageDialog(frame, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    private Polygon create(double off, Point2D... poly){
        int npoints = poly.length;
        int xpoints[] = new int[npoints];
        int ypoints[] = new int[npoints];
        for(int i=0; i<poly.length; i++){
            xpoints[i] = (int)(zoon*(poly[i].getX()-map.minX)+off);
            ypoints[i] = (int)(zoon*(poly[i].getY()-map.minY)+off);
        }
        return new Polygon(xpoints, ypoints, npoints);
    }
    public void pintar_cartesian(Graphics g, final boolean log) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, width-1, heigth-1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width-1, heigth-1);
        //g2.translate(cx, cy);
        //g2.scale(zoon, zoon);
    
        final int off = 120;
        zoon = Math.min((width-off-5)/(map.maxX-map.minX), (heigth-off-5)/(map.maxY-map.minY));
        /*if(TEMP_FIX){
            zoon = 30;
        }*/
        
        g2.setColor(Color.WHITE);
        g2.fillRect(off, off, (int)(zoon*(map.maxX-map.minX)), (int)(zoon*(map.maxY-map.minY)));
        g2.setColor(Color.BLACK);
        g2.drawRect(off, off, (int)(zoon*(map.maxX-map.minX)), (int)(zoon*(map.maxY-map.minY)));
        
         
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        
        
        double step =  Math.max(map.maxX-map.minX, map.maxY-map.minY)*1000*2/(15-1);
        
        if(step>1000){
            step = (int)(step/1000) * 1000;
        }else if(step>100){
            step = (int)(step/1000) * 100;
            //step /= 2;
            //step = (int)(step/100) * 100;
        }else if(step>10){
            step = (int)(step/10) * 10;
        }else if(step>1){
            step = (int)(step);
        }else{
            step = 1;
        }
        if(TEMP_FIX){
            step = 2000;
        }
        
        double Delta_CA = 0;
        for(double [][] vMUt : listMU){
            Delta_CA = 0;
            double vDt[] = null;
            if(listD.size()>0){
                vDt = listD.get(0);
                for(int t=0; t<vDt.length; t++){
                    Delta_CA += vDt[t];
                }
            }
        }
        
        g2.drawString(String.format("step = %1.1f  | [sum(delta)=%g] <= [DELTA=%g] | zoom = %f", step, Delta_CA, map.DELTA, zoon), 10, 15);
        
        g2.drawString(String.format("obj = %12g  |  dist = %12g | gap = %12g | time = %12g", obj, dist, gap, time), 10, 35);
        //double val = ((int)((minX*3600)/step))*step/3600;
        int val = (int)(((int)((map.minX*3600)/step))*step);
        val += map.minX < 0 ? -step : step;
        while(val<map.minX*1000){
            val += step;
        }
        while(val<map.maxX*1000){
            double degrees = val/1000;
            int x = (int)(zoon*(val/1000.0-map.minX)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(format, degrees), x-format_X, off-10);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, off, x, off+(int)(zoon*(map.maxY-map.minY)));
            
            val += step;
        }
        val = (int)(((int)((map.minY*1000)/step))*step);
        val += map.minY < 0 ? -step : step;
        while(val<map.minY*1000){
            val += step;
        }
        while(val<map.maxY*1000){
            double degrees = -val/1000.0;
            int y = (int)(zoon*(val/1000.0-map.minY)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(format, degrees), format_Y, y+4);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(off, y, off+(int)(zoon*(map.maxX-map.minX)), y);
            
            val += step;
        }
        
        if(INVERT_Y){
            g2.scale(1.0, -1.0);
            g2.translate(0, -(int)(zoon*(map.maxY-map.minY)+2*off));
        }
        g2.setColor(Color.BLACK);
        //g2.fillRect(-10+(int)(zoon*(map.Xgoal[0]-map.minX)+off), -10+(int)(zoon*(map.Xgoal[2]-map.minY)+off), 20, 20);
        g2.fillOval(-10+(int)(zoon*(map.X0[0]-map.minX)+off),    -10+(int)(zoon*(map.X0[2]-map.minY)+off),    20, 20);
        g2.drawRect(-800, -1100, 1400, 1100);
        
        for(int j=0; j<map.J; j++){
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillPolygon(create(off, map.Ob[j].points));
            
            plot_line(g2, map.Ob[j], off, step, Color.BLACK, Color.BLUE);
        }
        for(int j=0; j<map.Dy.length; j++){
            for(int t=0; t<map.T+1; t++){
                if(t==t_plot){
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.format("%2d", t_plot), 
                            (int)(zoon*((map.Dy[j][t].Cx-0.5)-map.minX)+off),
                            (int)(zoon*((map.Dy[j][t].Cy+0.25)-map.minY)+off));
                        
                    g2.setColor(new Color(255, 0, 0, 128));
                }else{
                    g2.setColor(new Color(255, 200, 0, 64));
                }
                g2.fillPolygon(create(off, map.Dy[j][t].points));
                plot_line(g2, map.Dy[j][t], off, step, Color.RED, Color.BLUE);
                
            }
        }
        for(int j=0; j<map.In.length; j++){
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%2d", j), 
                    (int)(zoon*((map.In[j].Cx-0.5)-map.minX)+off),
                    (int)(zoon*((map.In[j].Cy+0.25)-map.minY)+off));
            g2.setColor(new Color(0, 255, 0, 64));
            g2.fillPolygon(create(off, map.In[j].points));
            plot_line(g2, map.In[j], off, step, Color.GREEN, Color.BLUE);
        }
        
        g2.setStroke(new BasicStroke(3)); 
        for(int Zjti[][][] : listZ){
            int t = tt;
            g2.setColor(Color.BLUE);
            for(int j=0; j<map.J; j++){
                for(int i : Zjti[j][t]){
                    plot_line(g2, j, i, off, step, Color.BLUE, Color.BLUE);
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
        

        for(double [][] vMUt : listMU){
            double vDt[] = null;
            if(listD.size()>0){
                vDt = listD.get(0);
            }
            g2.setColor(Color.red);
            if(tt==-1){
                for(int t=0; t<map.T; t++){
                    final int r = 3;
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];
                    double x2 = vMUt[t+1][0];
                    double y2 = vMUt[t+1][2];
                    g2.drawLine((int)(zoon*(x1-map.minX)+off), (int)(zoon*(y1-map.minY)+off), (int)(zoon*(x2-map.minX)+off), (int)(zoon*(y2-map.minY)+off));
                    g2.fillOval((int)(zoon*(x1-map.minX)+off-r), (int)(zoon*(y1-map.minY)+off-r), 2*r, 2*r);
                    g2.fillOval((int)(zoon*(x2-map.minX)+off-r), (int)(zoon*(y2-map.minY)+off-r), 2*r, 2*r);
                }
                for(int t=0; t<map.T+1; t++){
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];

                    double rx = 0;
                    double ry = 0;
                    for(int j=0; j<4; j++){
                        rx += map.sum_t[t][0][j];
                        ry += map.sum_t[t][2][j];
                    }
                    double cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*map.DELTA);
                    double cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*map.DELTA);

                    if(t==t_plot){
                        g2.setColor(Color.BLACK);
                        g2.drawString(String.format("%2d", t_plot), (int)(zoon*((x1+cx)-map.minX)+off), (int)(zoon*((y1)-map.minY)+off));
                        g2.setColor(new Color(255, 0, 0, 128));
                        g2.fillOval((int)(zoon*((x1-cx)-map.minX)+off), (int)(zoon*((y1-cy)-map.minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                    }
                    g2.setColor(Color.RED);
                    g2.drawOval((int)(zoon*((x1-cx)-map.minX)+off), (int)(zoon*((y1-cy)-map.minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                    
                    
                    if(vDt!=null && log){
                        double delta = vDt[t];
                        cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*delta);
                        cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*delta);

                        if(t==t_plot){
                            g2.setColor(new Color(0, 0, 255, 128));
                            g2.fillOval((int)(zoon*((x1-cx)-map.minX)+off), (int)(zoon*((y1-cy)-map.minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                        }
                        g2.setColor(Color.BLUE);
                        g2.drawOval((int)(zoon*((x1-cx)-map.minX)+off), (int)(zoon*((y1-cy)-map.minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                        
                        if(PLOT_NORM){
                            g2.setColor(Color.BLACK);
                            g2.drawString(String.format("%g", vDt[t]),(int)(zoon*((x1+cx)-map.minX)+off), (int)(zoon*((y1)-map.minY)+off));
                        }
                    }
                }
            }else{
                int t = tt;
                double x1 = vMUt[t][0];
                double y1 = vMUt[t][2];

                double rx = 0;
                double ry = 0;
                for(int j=0; j<4; j++){
                    rx += map.sum_t[t][0][j];
                    ry += map.sum_t[t][2][j];
                }
                double cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*map.DELTA);
                double cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*map.DELTA);
                g2.drawOval((int)(zoon*((x1-cx)-map.minX)+off), (int)(zoon*((y1-cy)-map.minY)+off), (int)(zoon*((2*cx))+off), (int)(zoon*((2*cy))+off));
            }
                
        }
        
        if(INVERT_Y){
            g2.translate(0.0, +(zoon*(map.maxY-map.minY)+2*off));
            g2.scale(1.0, -1.0);
        }
        for(int j=0; j<map.J; j++){
            g2.setColor(Color.BLUE);    
            //g2.drawString(String.format("[%d]", j), (int)(zoon*(xm-minX)+off)-20, (int)(zoon*(ym-minY)+off)+5);
            if(j>9){
                g2.drawString(String.format("%2d", j+1), (int)(zoon*(map.Ob[j].Cx-map.minX)+off)-10, (int)(zoon*(map.Ob[j].Cy-map.minY)+off)+5);
            }else{
                g2.drawString(String.format("%2d", j+1), (int)(zoon*(map.Ob[j].Cx-map.minX)+off)-15, (int)(zoon*(map.Ob[j].Cy-map.minY)+off)+5);
            }
        }
    }
    private void plot_line(Graphics2D g2, int j, int i, int off, double step, Color Color_line, Color Color_norm) {
        double x1, y1;
        if(Math.abs(map.Ob[j].lines[i].ay)>0.01){
            x1 = map.Ob[j].points[i].getX();
            y1 = ( map.Ob[j].lines[i].b - map.Ob[j].lines[i].ax * x1 ) / map.Ob[j].lines[i].ay;
        }else{
            y1 = map.Ob[j].points[i].getY();
            x1 = ( map.Ob[j].lines[i].b - map.Ob[j].lines[i].ay * y1 ) / map.Ob[j].lines[i].ax;
        }
        int k = (i+1) % map.Ob[j].length();
        double x2, y2;
        if(Math.abs(map.Ob[j].lines[k].ay)>0.01){
            x2 = map.Ob[j].points[k].getX();
            y2 = ( map.Ob[j].lines[k].b - map.Ob[j].lines[k].ax * x2 ) / map.Ob[j].lines[k].ay;
        }else{
            y2 = map.Ob[j].points[k].getY();
            x2 = ( map.Ob[j].lines[k].b - map.Ob[j].lines[k].ay * y2 ) / map.Ob[j].lines[k].ax;
        }
        g2.setColor(Color_line);
        g2.drawLine((int)(zoon*(x1-map.minX)+off), (int)(zoon*(y1-map.minY)+off), (int)(zoon*(x2-map.minX)+off), (int)(zoon*(y2-map.minY)+off));
        //g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
        //g2.drawPolygon(poly[j]);
        if(PLOT_NORM){
            double S = step/(map.Ob[j].lines[i].norm() * 5000);
            g2.setColor(Color_norm);
            g2.drawLine((int)(zoon*((x1+x2)/2-map.minX))+off, (int)(zoon*((y1+y2)/2-map.minY))+off, (int)(zoon*((x1+x2)/2-map.minX+S*map.Ob[j].lines[i].ax))+off, (int)(zoon*((y1+y2)/2-map.minY+S*map.Ob[j].lines[i].ay))+off);
            g2.fillOval((int)(zoon*((x1+x2)/2-map.minX))+off-5, (int)(zoon*((y1+y2)/2-map.minY))+off-5, 10, 10);
            g2.fillOval((int)(zoon*((x1+x2)/2-map.minX+S*map.Ob[j].lines[i].ax))+off-5, (int)(zoon*((y1+y2)/2-map.minY+S*map.Ob[j].lines[i].ay))+off-5, 10, 10);
        }
    }
    private void plot_line(Graphics2D g2, Obstacle poly, int off, double step, Color Color_line, Color Color_norm) {
        for(int i=0; i<poly.length(); i++){
            double x1, y1;
            if(Math.abs(poly.lines[i].ay)>0.01){
                x1 = poly.points[i].getX();
                y1 = ( poly.lines[i].b -poly.lines[i].ax * x1 ) / poly.lines[i].ay;
            }else{
                y1 = poly.points[i].getY();
                x1 = ( poly.lines[i].b - poly.lines[i].ay * y1 ) / poly.lines[i].ax;
            }
            int k = (i+1) % poly.length();
            double x2, y2;
            if(Math.abs(poly.lines[k].ay)>0.01){
                x2 = poly.points[k].getX();
                y2 = ( poly.lines[k].b - poly.lines[k].ax * x2 ) / poly.lines[k].ay;
            }else{
                y2 = poly.points[k].getY();
                x2 = ( poly.lines[k].b - poly.lines[k].ay * y2 ) / poly.lines[k].ax;
            }
            g2.setColor(Color_line);
            g2.drawLine((int)(zoon*(x1-map.minX)+off), (int)(zoon*(y1-map.minY)+off), (int)(zoon*(x2-map.minX)+off), (int)(zoon*(y2-map.minY)+off));
            //g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
            //g2.drawPolygon(poly[j]);
            if(PLOT_NORM){
                double S = step/(poly.lines[i].norm() * 5000);
                g2.setColor(Color_norm);
                g2.drawLine((int)(zoon*((x1+x2)/2-map.minX))+off, (int)(zoon*((y1+y2)/2-map.minY))+off, (int)(zoon*((x1+x2)/2-map.minX+S*poly.lines[i].ax))+off, (int)(zoon*((y1+y2)/2-map.minY+S*poly.lines[i].ay))+off);
                g2.fillOval((int)(zoon*((x1+x2)/2-map.minX))+off-5, (int)(zoon*((y1+y2)/2-map.minY))+off-5, 10, 10);
                g2.fillOval((int)(zoon*((x1+x2)/2-map.minX+S*poly.lines[i].ax))+off-5, (int)(zoon*((y1+y2)/2-map.minY+S*poly.lines[i].ay))+off-5, 10, 10);
            }
        }
    }
}
