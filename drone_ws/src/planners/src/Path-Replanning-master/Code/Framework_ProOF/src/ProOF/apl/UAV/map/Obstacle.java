/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.map;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import java.awt.Rectangle;
import java.awt.geom.Point2D;


/**
 *
 * @author marcio
 */
public abstract class Obstacle {
    public final static boolean PRINT = false;
    //public abstract void paint();
    public Hyperplane hyperplans[];
    public Rectangle rect;
    
    public abstract void paint(Graphics2DReal gr);
    public abstract void paint_sense(Graphics2DReal gr, double r, double f);
    
    public final int Gj(){
        return hyperplans.length;
    }    
    public final Hyperplane plane(int i){
        return hyperplans[i]; 
    }
    
    /**
     * Calculate the distance form a point p to this obstacle
     * @param p
     * @return
     * @throws Exception 
     */
    public final double distance(double p[]) throws Exception{
        double max = Double.NEGATIVE_INFINITY;
        for(int i=0; i<Gj(); i++){
            //c_(i,t) (δ_jt ) ≤ a_ji^T*μ_t - b_ji
            double c = hyperplans[i].scalarProd(p) - hyperplans[i].b;
            max = Math.max(max, c);
        }
        return max>0 ? max : 0.0;
    }
    /**
     * Calculate the distance form a point p to the ith hyperplane of obstacle
     * @param i
     * @param p
     * @return max( 0 , a^T * p - b)
     */
    public final double distance(int i, double p[]){
        double c = hyperplans[i].scalarProd(p) - hyperplans[i].b;
        return Math.max(c, 0.0);
    }
    

    
    public final int indexNear(double[] p) {
        int k = 0;
        double max = Double.NEGATIVE_INFINITY;
        for(int i=0; i<Gj(); i++){
            //c_(i,t) (δ_jt ) ≤ a_ji^T*μ_t - b_ji
            double c = hyperplans[i].scalarProd(p) - hyperplans[i].b;
            if(c>max){
                max = c;
                k=i;
            }
        }
        return k;
    }
    
    public static double trueAllocation(double x[], Obstacle ...obs) throws Exception{
        double alloc = Double.POSITIVE_INFINITY;
        for(Obstacle o : obs){
            alloc = Math.min(alloc, o.distance(x));
        }
        return alloc;
    }
    public static int indexNear(double[] p, Obstacle ...obs) throws Exception{
        int k = 0;
        double dist = Double.POSITIVE_INFINITY;
        for(int j=0; j<obs.length; j++){
            double val = obs[j].distance(p);
            if(val<dist){
                dist = val;
                k = j;
            }
        }
        return k;
    }
    
    
    public static Rectangle rectangleFrom(Point2D points[]){
        Rectangle rect = new Rectangle();
        for(Point2D p:points){
            rect.add(p);
        }
        return rect;
    }
    public static Hyperplane[] hyperplansFrom(Point2D points[], Hyperplane ...add){
        Hyperplane[] hyperplans = new Hyperplane[points.length+add.length];
        for(int i=0; i<points.length; i++){
            int k = (i+1) % points.length;
            double x1 = points[i].getX();
            double y1 = points[i].getY();
            double x2 = points[k].getX();
            double y2 = points[k].getY();

            //x'
            double ax = -( y2 - y1 );
            //y'
            double ay = +( x2 - x1 );            
            
            //normalize
            double norm = Math.sqrt(ax*ax+ay*ay);
            ax /= norm;
            ay /= norm;

            double b = ax * x1  + ay * y1;

            if(add==null || add.length==0){
                hyperplans[i] = new Hyperplane(b, ax, ay);
            }else{
                hyperplans[i] = new Hyperplane(b, ax, ay, 0.0);
            }
            //if(PRINT)System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  = %8.4f\n", ax, ay, b);
        }
        System.arraycopy(add, 0, hyperplans, points.length, add.length);
        
        return hyperplans;
    }
    
    public static Hyperplane[] hyperplansFrom3D(Point2D points[]){
        Hyperplane[] hyperplans = new Hyperplane[points.length];
        for(int i=0; i<points.length; i++){
            int k = (i+1) % points.length;
            double x1 = points[i].getX();
            double y1 = points[i].getY();
            double x2 = points[k].getX();
            double y2 = points[k].getY();

            //x'
            double ax = -( y2 - y1 );
            //y'
            double ay = +( x2 - x1 );            
            
            //normalize
            double norm = Math.sqrt(ax*ax+ay*ay);
            ax /= norm;
            ay /= norm;

            double b = ax * x1  + ay * y1;

            hyperplans[i] = new Hyperplane(b, ax, ay, 0.0);            
        }        
        return hyperplans;
    }
}
