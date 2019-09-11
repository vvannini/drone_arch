/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import ProOF.apl.UAV.map.Hyperplane;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Locale;


/**
 *
 * @author marcio
 */
public class Graphics2DReal {
    public final Graphics2D g2;
    public final int UNIT;
    
    public Graphics2DReal(Graphics2D g2, int UNIT) {
        this.g2 = g2;
        this.UNIT = UNIT;
    }
    
    public final int unit(double v){
        return (int)(v*UNIT);
    }
    
    public void setColor(Color color) {
        g2.setColor(color);
    }
    
    public final void drawOvalR(double x, double y, double rx, double ry){
        g2.drawOval(unit(x-rx), unit(y-ry), unit(2*rx), unit(2*ry));
    }
    public final void fillOvalR(double x, double y, double rx, double ry){
        g2.fillOval(unit(x-rx), unit(y-ry), unit(2*rx), unit(2*ry));
    }
    public final void drawRectR(double x, double y, double rx, double ry){
        g2.drawRect(unit(x-rx), unit(y-ry), unit(2*rx), unit(2*ry));
    }
    public final void fillRectR(double x, double y, double rx, double ry){
        g2.fillRect(unit(x-rx), unit(y-ry), unit(2*rx), unit(2*ry));
    }
    public void drawLineR(double x1, double y1, double x2, double y2) {
        g2.drawLine(unit(x1), unit(y1), unit(x2), unit(y2));
    }
    
    public void paintOvalR(double x, double y, double rx, double ry, Color fill, Color draw){
        g2.setColor(fill);
        fillOvalR(x, y, rx, ry);
        g2.setColor(draw);
        drawOvalR(x, y, rx, ry);
    }
    public void paintRectR(double x, double y, double rx, double ry, Color fill, Color draw){
        g2.setColor(fill);
        fillRectR(x, y, rx, ry);
        g2.setColor(draw);
        drawRectR(x, y, rx, ry);
    }
    public void paintLineR(double x1, double y1, double x2, double y2, Color draw) {
        g2.setColor(draw);
        g2.drawLine(unit(x1), unit(y1), unit(x2), unit(y2));
    }

    public void fillPolygon(Polygon poly) {
        g2.fillPolygon(poly);
    }

    public void drawPolygon(Polygon poly) {
        g2.drawPolygon(poly);
    }

    public void addPoint(Polygon poly, Point2D p) {
        poly.addPoint(unit(p.getX()), unit(p.getY()));
    }

    public Polygon polygon(Point2D[] points) {
        Polygon poly = new Polygon();
        for(Point2D p : points){
            addPoint(poly, p);
        }
        return poly;
    }

    public void paintPolygon(Point2D[] points, Color fill, Color draw) {
        Polygon poly = polygon(points);
        setColor(fill);
        fillPolygon(poly);
        setColor(draw);
        drawPolygon(poly); 
    }
    public void paintPolygon(Point2D[] points, String name, Font font, Color fill, Color draw) {
        paintPolygon(points, fill, draw);
        setColor(draw);
        double mx = averageX(points);
        double my = averageY(points);
        drawStringR(name, font, mx, my);
    }
    private static double averageX(Point2D[] points){
        double sum = 0;
        for(Point2D p : points){
            sum += p.getX();
        }
        return sum/points.length;
    }
    private static double averageY(Point2D[] points){
        double sum = 0;
        for(Point2D p : points){
            sum += p.getY();
        }
        return sum/points.length;
    }
    
    public void paintNormal(Point2D[] points, Hyperplane[] hyperplans, double r, double f, Color fill) {
        setColor(fill);    //paint normal
        for(int i=0; i<points.length; i++){
            int j = (i+1)%points.length;
            double mx = (points[i].getX()+points[j].getX())/2;
            double my = (points[i].getY()+points[j].getY())/2;
            drawLineR(mx, my, mx+hyperplans[i].a[0]*f, my+hyperplans[i].a[1]*f);
            fillOvalR(mx, my, r, r);
            fillOvalR(mx+hyperplans[i].a[0]*f, my+hyperplans[i].a[1]*f, r, r);
        }
    }

    public void drawStringR(String format, Font font, double x, double y, Object ...args) {
        setFont(font);
        drawStringR(format, x, y, args);
    }
    public void drawStringR(String format, double x, double y, Object ...args) {
        Font font = g2.getFont();
        String str = String.format(Locale.ENGLISH, format, args); //"Olá mundo";
        int fw = (int) (font.getStringBounds(str, g2.getFontRenderContext()).getWidth()/2);
        int fh = (int) (font.getStringBounds(str, g2.getFontRenderContext()).getHeight()/4);
        
        //drawRectR(x, y, fw/100.0, fh/50.0);
        
        g2.drawString(str, unit(x)-fw, unit(y)+fh);
    }

    public void setFont(Font font) {
        g2.setFont(font);
    }

    public void draw(Rectangle rect, Color color) {
        g2.setColor(color);
        g2.drawRect(unit(rect.x), unit(rect.y), unit(rect.width), unit(rect.height));
    }
    public void draw(Shape draw_region) {
        Rectangle r = draw_region.getBounds();
        double cx = r.x+r.width/2;
        double cy = r.y+r.height/2;
        
        g2.translate(cx, cy);
        g2.scale(UNIT*1.0, UNIT*1.0);
        g2.draw(draw_region);
        g2.scale(1.0/UNIT, 1.0/UNIT);
        g2.translate(-cx, -cy);
    }

    
    
    
}
