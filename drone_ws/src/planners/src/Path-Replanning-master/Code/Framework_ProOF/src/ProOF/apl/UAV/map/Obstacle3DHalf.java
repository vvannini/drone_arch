/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.map;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.utilities.uIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class Obstacle3DHalf extends Obstacle{
    
    private final double height;
    private final Point2D points[];
    private final Color color;
    private final String name;
    private final Font font;
    
    public Obstacle3DHalf(Color color, String name, Font font, Scanner sc, double h) {
        height = h;
        this.color = color;
        this.name = name;
        this.font = font;        
        sc.nextLine();
        double x[] = uIO.toVectorDouble(sc.nextLine());
        double y[] = uIO.toVectorDouble(sc.nextLine());
        this.points = new Point2D[x.length];
        for(int i=0; i<x.length; i++){
            points[i] = new Point2D.Double(x[i], y[i]);
        }        
        this.hyperplans = hyperplansFrom3D(points);
        this.rect = rectangleFrom(points);
    }
    
    public Obstacle3DHalf(double height, Color color, String name, Font font, Scanner sc) {
        this.height = height;
        this.color = color;
        this.name = name;
        this.font = font;        
        sc.nextLine();
        double x[] = uIO.toVectorDouble(sc.nextLine());
        double y[] = uIO.toVectorDouble(sc.nextLine());
        this.points = new Point2D[x.length];
        for(int i=0; i<x.length; i++){
            points[i] = new Point2D.Double(x[i], y[i]);
        }        
        this.hyperplans = hyperplansFrom(points, new Hyperplane(height, 0, 0, 1));
        this.rect = rectangleFrom(points);
    }
    public Obstacle3DHalf(double height, Obstacle3DHalf base) {
        this.height = height;
        this.color =  Color.lightGray;
        this.name = base.name;
        this.font = base.font;
        this.points = new Point2D[base.points.length];
        for(int i=0; i<points.length; i++){
            points[i] = new Point2D.Double(base.points[i].getX(), base.points[i].getY());
        }        
        this.hyperplans = hyperplansFrom(points, new Hyperplane(height, 0, 0, 1));
        this.rect = rectangleFrom(points);
    }
    
    public Obstacle3DHalf(double height_max, double dist_ref, 
            double cx_ref, double cy_ref, Font font, Scanner sc) {
        this.font = font;
        
        sc.nextLine();
        double x[] = uIO.toVectorDouble(sc.nextLine());
        double y[] = uIO.toVectorDouble(sc.nextLine());
        this.points = new Point2D[x.length];
        for(int i=0; i<x.length; i++){
            points[i] = new Point2D.Double(x[i], y[i]);
        }        
        
        double cx = centerX();
        double cy = centerY();
        double dist_center = Math.sqrt((cx-cx_ref)*(cx-cx_ref) + (cy-cy_ref)*(cy-cy_ref));
        this.height = height_max*(dist_ref/(dist_center+dist_ref));
        int rgb = (int) (255 - (height*(200))/height_max);
        this.color = new Color(rgb,rgb,rgb); 
        this.name = String.format(Locale.ENGLISH, "%1.1f", height);
        
        Hyperplane top = new Hyperplane(height_max, 0, 0, 1);
        this.hyperplans = hyperplansFrom(points, top);
        this.rect = rectangleFrom(points);
        //if(PRINT)System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  + %8.4f * z  = %8.4f\n", 0.0, 0.0, 1.0, height_max);
    }
    
    public Obstacle3DHalf(double height, String name, Font font) {
        this.height = height;
        this.name = name;
        this.font = font;
        this.color = null;
        
        this.points = null;
        this.rect = null;
        hyperplans = new Hyperplane[]{
            new Hyperplane(height, 0, 0, 1)
        };
        if(PRINT) System.out.println("---------------------- ground -------------------------");
        if(PRINT) System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  + %8.4f * z  = %8.4f\n", 0.0, 0.0, 1.0, height);
    }
    
    private double centerX(){
        double sum = 0;
        for(Point2D p : points){
            sum += p.getX();
        }
        return sum/points.length;
    }
    
    private double centerY(){
        double sum = 0;
        for(Point2D p : points){
            sum += p.getY();
        }
        return sum/points.length;
    }
    
    public double getHeight(){
        return height;
    }
    
    @Override
    public void paint(Graphics2DReal gr) {
        if(points!=null){   // not paint the ground
            gr.paintPolygon(points, name, font, color, Color.BLACK);
        }
    }
    
    @Override
    public void paint_sense(Graphics2DReal gr, double r, double f) {
        if(points!=null){   // not paint the ground
            gr.paintNormal(points, hyperplans, r, f, Color.BLUE);
        }
    }    
}
