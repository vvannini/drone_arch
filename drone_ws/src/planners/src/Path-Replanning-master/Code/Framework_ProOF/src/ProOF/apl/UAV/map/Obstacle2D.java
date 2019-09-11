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
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class Obstacle2D extends Obstacle{
    
    private final Point2D points[];
    private final String name;
    private final Font font;
    private final Color color;
    
    public Obstacle2D(String name, Font font, Scanner sc) {
        this(Color.LIGHT_GRAY, name, font, sc);
    }
    
    public Obstacle2D(Color color, String name, Font font, Scanner sc) {
        this.name = name;
        this.font = font;
        this.color = color;
        sc.nextLine();
        double x[] = uIO.toVectorDouble(sc.nextLine());
        double y[] = uIO.toVectorDouble(sc.nextLine());
        this.points = new Point2D[x.length];
        for(int i=0; i<x.length; i++){
            points[i] = new Point2D.Double(x[i], y[i]);
        }
        this.hyperplans = hyperplansFrom(points);
        this.rect = rectangleFrom(points);
    }
    
    public Obstacle2D(String name, Font font, Point2D ...points) {
        this.name = name;
        this.font = font;
        this.color = Color.LIGHT_GRAY;
        this.points = points;
        this.hyperplans = hyperplansFrom(points);
        this.rect = rectangleFrom(points);
    }    

    @Override
    public void paint(Graphics2DReal gr) {
        gr.paintPolygon(points, name, font, color, Color.BLACK);
    }
    
    @Override
    public void paint_sense(Graphics2DReal gr, double r, double f) {
        gr.paintNormal(points, hyperplans, r, f, Color.BLUE);
    }
}
