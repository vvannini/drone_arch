/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.pog.problem.CCQSP;


import ProOF.com.Linker.LinkerResults;
import ProOF.utilities.uIO;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class CCQSPMap {
    
    public double minX, minY;
    public double maxX, maxY;
    
    public int J;
    public int E = 0;
    public int I = 0;
    
    
    public double Rjti[][][];
    public double sum_t[][][];
    public double sum_Ut[][][];
    //public double aji[][][];
    //public double bji[][];
    
    public Obstacle[] Ob = null;
    public Obstacle Dy[][] = null;
    public Obstacle In[] = null;
    public Obstacle U = null;
    
    public final double M = 1e6;    //if M>0 -> Mjti=M and Ejti=M 
    public double Mjti[][][];
    public double Ejti[][][];
    
    public final double X0[] = new double[]{
        0, 0, 0, 0
    };
    public int Identificador = 1;
    
    
    public double VMAX = 3;
    //public double VMIN = 0;
    public double UMAX = 1;
    public double DELTA = 0.05;
    
    public double SD = 0.006;
    public double TEMPO = 20;
    
    public final int DN = defineDN();
    
    public int T = 10;
    public double DT = 1;
    
    public void results(LinkerResults win) throws Exception {
        win.writeDbl("inst-DELTA", DELTA);
        win.writeInt("inst-T", T);
        win.writeInt("inst-J", J);
        win.writeInt("inst-E", E);
    }

    public final static int defineDN(){
        int dm = 15;//11;//7 - (int)(Math.log10(inst.DELTA));
        //int dm = 9;//11;//7 - (int)(Math.log10(inst.DELTA));
        //27 -- 0.1         -- 28-1     
        //24 -- 0.01        -- 26-2     //-2
        //18 -- 0.001       -- 21-3     //-5
        //7  -- 0.0001      -- 11-4     //10
        //
        System.out.println("dm = "+dm);
        final int DN = dm < 2 ? 2 : dm > 32 ? 32 : dm;   //DM in [2 ... 32]
        return DN;
    }
 
    
    public void start(File file_map, CCQSPDynamic dy) throws FileNotFoundException {
        Scanner sc = new Scanner(file_map);
        sc.nextLine();
        J = Integer.parseInt(sc.nextLine());
        //J = 0;
        //poly = new Polygon[J];
        
        minX = X0[0];
        minY = X0[2];
        maxX = X0[0];
        maxY = X0[2];
        
        //poly = new Point2D[J][];
        Ob = new Obstacle[J];
        for(int j=0; j<J; j++){
            sc.nextLine();
            double x[] = uIO.toVectorDouble(sc.nextLine());
            double y[] = uIO.toVectorDouble(sc.nextLine());
            Point2D points[] = new Point2D[x.length];
            for(int i=0; i<x.length; i++){
                points[i] = new Point2D.Double(x[i], y[i]);
                minX = Math.min(minX, x[i]);
                minY = Math.min(minY, y[i]);
                maxX = Math.max(maxX, x[i]);
                maxY = Math.max(maxY, y[i]);
            }
            Ob[j] = new Obstacle(points);
            //poly[j] = new Polygon(x, y, x.length);
        }
        
        sc.nextLine();
        I = Integer.parseInt(sc.nextLine());
        In = new Obstacle[I];
        for(int j=0; j<I; j++){
            sc.nextLine();
            double x[] = uIO.toVectorDouble(sc.nextLine());
            double y[] = uIO.toVectorDouble(sc.nextLine());
            Point2D points[] = new Point2D[x.length];
            for(int i=0; i<x.length; i++){
                points[i] = new Point2D.Double(x[i], y[i]);
                minX = Math.min(minX, x[i]);
                minY = Math.min(minY, y[i]);
                maxX = Math.max(maxX, x[i]);
                maxY = Math.max(maxY, y[i]);
            }
            In[j] = new Obstacle(points);
            //poly[j] = new Polygon(x, y, x.length);
        }
        
//        In = new Obstacle[I][T+1];
//        for(int t=0; t<T+1; t++){
//            if(I>0){
//                if(t==1+T/2){
//                    In[0][t] = new Obstacle(create(-3, -8, 1, 0.0));
//                }
//            }
//        }
        
        sc.close();
        
        if(CCQSPPlot.TEMP_FIX){
//            minX = -6;
//            maxX = 6;
//            minY = -10;
//            maxY = 0;
            
            minX = 0;
            maxX = 24;
            minY = -18;
            maxY = -6;
        }
        
        double temp = Math.max(maxX-minX, maxY-minY);
        minX -= temp*CCQSPPlot.offset;
        minY -= temp*CCQSPPlot.offset;
        maxX += temp*CCQSPPlot.offset;
        maxY += temp*CCQSPPlot.offset;
        
        
        Dy = new Obstacle[E][T+1];
        int div = 4;
        for(int t=0; t<T+1; t++){
            if(E>0){
            //Dy[0][t] = new Obstacle(create(maxX-t*(maxX-minX)/(T)-1, (minY+maxY)/2, (T*2.0)/(T+Math.abs(2*t-T)), (t*2*Math.PI)/T));
            //Dy[0][t] = new Obstacle(create(maxX-(t%div)*(maxX-minX)/(div-1), (minY+maxY)/2, (T*2.0)/(T+Math.abs(2*t-T)), (t*2*Math.PI)/T));
            //Dy[0][t] = new Obstacle(create(maxX-(t%div)*(maxX-minX)/(div-1), (minY+maxY)/2, ((t+T))/(T*1.0), (t*2*Math.PI)/T));
            
//            Dy[0][t] = new Obstacle(create(-4, maxY-t*(maxY-minY)/(T), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
//            Dy[1][t] = new Obstacle(create(+4, maxY-t*(maxY-minY)/(T), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
            
//            Dy[0][t] = new Obstacle(create(-5, 0+minY+t*(maxY-minY)/(T*1.0), ((t+T))/(T*3.0), 0.0));
//            Dy[1][t] = new Obstacle(create(+4, 4+minY+t*(maxY-minY)/(T*1.5), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
//            
//            Dy[0][t] = new Obstacle(create(5, -4+maxY-t*(maxY-minY)/(T*3), ((t+T))/(T*3.0), 0.0));
//            Dy[1][t] = new Obstacle(create(-3+maxX-((t+5)%div)*(maxX-minX)/(div*1.3-1), -3, ((t+T))/(T*1.5), 0.0));
              Dy[0][t] = new Obstacle(create(-4, 5+minY+t*(maxY-minY)/(T*2.0), ((t+T))/(T*2.0), 0.0));
              Dy[1][t] = new Obstacle(create(-2.0+maxX-(t)*(maxX-minX)/(T*1.7-1), -7+maxY-t*(maxY-minY)/(T*3.0), ((t+T))/(T*2.0), 0.0));
              Dy[2][t] = new Obstacle(create(-6+maxX-(t)*(maxX-minX)/(T*6.6-1), 8+minY+t*(maxY-minY)/(T*4.0), ((t+T))/(T*2.0), 0.0));
            }
        }
//        for(int i=0; i<4; i++){
//            Xgoal[i] = X0[i];
//        }
        
        sum_t = new double[T+1][4][4];
        
        for(int t=0; t<T+1; t++){
            
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    sum_t[t][i][j] = dy.P0[i][j];
                }
            }
//            sum_t[t] = prod(pow(dy.A,t), dy.P0, pow(trans(dy.A), t));
//            for(int i=0; i<t; i++){
//                sum_t[t] = sum(sum_t[t], prod(pow(dy.A,i), dy.Q, pow(trans(dy.A),i)));
//            }
        }
        
        sum_Ut = new double[T][2][2];
        for(int t=0; t<T; t++){
            sum_Ut[t][0][0] = Math.pow(SD, 2);
            sum_Ut[t][1][1] = Math.pow(SD, 2);
        }
        U = new Obstacle(create(0, 0, UMAX, 0.0));
        
        
        
        Rjti = new double[J][T+1][];
        for(int j=0; j<J; j++){
            for(int t=0; t<T+1; t++){
                Rjti[j][t] = new double[Ob[j].length()];
                for(int i=0; i<Ob[j].length(); i++){
                    Rjti[j][t][i] = Ob[j].R(i, sum_t[t]);
                }
            }
        }
        
        LinkedList<Point2D> points[] = new LinkedList[T+1];
        for(int t=0; t<T+1; t++){
            points[t] = new LinkedList<Point2D>();
            points[t].add(new Point2D.Double(X0[0], X0[2]));
            //points[t].add(new Point2D.Double(Xgoal[0], Xgoal[2]));
            for(int j=0; j<J; j++){
                for(int i=0; i<Ob[j].lines.length; i++){
                    points[t].add(new Point2D.Double(Ob[j].points[i].getX(), Ob[j].points[i].getY()));
                }
            }
            for(int j=0; j<E; j++){
                for(int i=0; i<Dy[j][t].lines.length; i++){
                    points[t].add(new Point2D.Double(Dy[j][t].points[i].getX(), Dy[j][t].points[i].getY()));
                }
            }
        }
            
        
        double max_sd = Integer.MIN_VALUE;
        double min_sd = Integer.MAX_VALUE;
        double max_M = Integer.MIN_VALUE;
        double min_M = Integer.MAX_VALUE;
        
        Mjti = new double[J][T+1][];
        for(int j=0; j<J; j++){
            for(int t=0; t<T+1; t++){
                Mjti[j][t] = new double[Ob[j].length()];
                for(int i=0; i<Ob[j].length(); i++){
                    double sd = Normal.inverseStandardCdf(1-2*DELTA/Math.pow(2, DN))*Ob[j].R(i, sum_t[t]);
                    Mjti[j][t][i] = M>0 ? M : 3*(max_dist(t, Ob[j].points[i], points) + sd);
                    max_sd = Math.max(max_sd, sd);
                    min_sd = Math.min(min_sd, sd);
                    
                    max_M = Math.max(max_M, Mjti[j][t][i]);
                    min_M = Math.min(min_M, Mjti[j][t][i]);
                }
            }
        }
        System.out.println("max_Mjti    = "+max_M);
        System.out.println("min_Mjti    = "+min_M);
        System.out.println("min_M-sd    = "+min_sd);
        System.out.println("max_M-sd    = "+max_sd);
        
        max_sd = Integer.MIN_VALUE;
        min_sd = Integer.MAX_VALUE;
        max_M = Integer.MIN_VALUE;
        min_M = Integer.MAX_VALUE;
        
        Ejti = new double[E][T+1][];
        for(int j=0; j<E; j++){
            for(int t=0; t<T+1; t++){
                Ejti[j][t] = new double[Dy[j][t].length()];
                for(int i=0; i<Dy[j][t].length(); i++){
                    double sd = Normal.inverseStandardCdf(1-2*DELTA/Math.pow(2, DN))*Dy[j][t].R(i, sum_t[t]);
                    Ejti[j][t][i] = M>0 ? M : 3*(max_dist(t, Dy[j][t].points[i], points) + sd);
                    max_sd = Math.max(max_sd, sd);
                    min_sd = Math.min(min_sd, sd);
                    
                    max_M = Math.max(max_M, Ejti[j][t][i]);
                    min_M = Math.min(min_M, Ejti[j][t][i]);
                }
            }
        }
        System.out.println("max_Ejti    = "+max_M);
        System.out.println("min_Ejti    = "+min_M);
        System.out.println("min_E-sd    = "+min_sd);
        System.out.println("max_E-sd    = "+max_sd);
    }
    
    private static double max_dist(int t, Point2D now, LinkedList<Point2D> all[]){
        double max_d = 0;
        for(Point2D p : all[t]){
            max_d = Math.max(max_d, now.distance(p));
        }
        return max_d;
    }
    private static Point2D[] create(double xm, double ym, double R, double ang){
        double x, y;
        Point2D square[] = new Point2D[4];
        x = (-R)*Math.cos(ang) - (-R)*Math.sin(ang);
        y = (-R)*Math.sin(ang) + (-R)*Math.cos(ang);
        square[0] = new Point2D.Double((x+xm), (y+ym));
        
        
        x = (-R)*Math.cos(ang) - (+R)*Math.sin(ang);
        y = (-R)*Math.sin(ang) + (+R)*Math.cos(ang);
        square[1] = new Point2D.Double((x+xm), (y+ym));
        
        
        x = (+R)*Math.cos(ang) - (+R)*Math.sin(ang);
        y = (+R)*Math.sin(ang) + (+R)*Math.cos(ang);
        square[2] = new Point2D.Double((x+xm), (y+ym));
        
        
        x = (+R)*Math.cos(ang) - (-R)*Math.sin(ang);
        y = (+R)*Math.sin(ang) + (-R)*Math.cos(ang);
        square[3] = new Point2D.Double((x+xm), (y+ym));
        
        return square;
    }
    
    public double centerX(int t, int j){
        double center = 0;
        for(int i=0; i<Ob[j].length(); i++){
            center += Ob[j].points[i].getX();
        }
        return center/Ob[j].length();
    }
    public double centerY(int t, int j){
        double center = 0;
        for(int i=0; i<Ob[j].length(); i++){
            center += Ob[j].points[i].getY();
        }
        return center/Ob[j].length();
    }
    public double raioX(int t, int j){
        double center = centerX(t, j);
        double max = 0;
        for(int i=0; i<Ob[j].length(); i++){
            max = Math.max(max, Math.abs(center-Ob[j].points[i].getX()));
        }
        return max;
    }
    public double raioY(int t, int j){
        double center = centerY(t, j);
        double max = 0;
        for(int i=0; i<Ob[j].length(); i++){
            max = Math.max(max, Math.abs(center-Ob[j].points[i].getY()));
        }
        return max;
    }
    
    public class Obstacle{
        public final Point2D points[];
        public final Line lines[];
        public final double Cx;
        public final double Cy;
        public final double R;
        public Obstacle(Point2D... points) {
            this.points = points;
            this.lines = new Line[points.length];
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
                
                double b = ax * x1  + ay * y1;
                
                lines[i] = new Line(ax, ay, b);
                
            }
            
            double xm = 0;
            double ym = 0;
            for (Point2D point : points) {
                xm += point.getX();
                ym += point.getY();
            }
            Cx = xm / points.length;
            Cy = ym / points.length;
            
            double r = 0;
            for (Point2D point : points) {
                r = Math.max(r, point.distance(Cx, Cy));
            }
            R = r;
        }
        
        public final int length(){
            return points.length;
        }
        public final double R(int i, double sum[][]){
            double prod = 
                    lines[i].ax*sum[0][0]*lines[i].ax+
                    lines[i].ay*sum[2][0]*lines[i].ax+
                    lines[i].ax*sum[0][2]*lines[i].ay+
                    lines[i].ay*sum[2][2]*lines[i].ay;
            return Math.sqrt(2*prod);
        }
        public final double U(int i, double sum[][]){
            double prod = 
                    lines[i].ax*sum[0][0]*lines[i].ax+
                    lines[i].ay*sum[1][0]*lines[i].ax+
                    lines[i].ax*sum[0][1]*lines[i].ay+
                    lines[i].ay*sum[1][1]*lines[i].ay;
            return Math.sqrt(2*prod);
        }
    }
    public class Line{
        public final double ax;
        public final double ay;
        public final double b;
        public Line(double ax, double ay, double b) {
            this.ax = ax;
            this.ay = ay;
            this.b = b;
        }
        public final double sqr_norm(){
            return ax*ax+ay*ay;
        }
        public final double norm(){
            return Math.sqrt(ax*ax+ay*ay);
        }
    }
    
    
        
    
    public static double[][] pow(double A[][], int n){
        if(n<=0){
            double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                R[i][i] = 1;
            }
            return R;
        }else{
            double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                for(int j=0; j<R[i].length; j++){
                    R[i][j] = A[i][j];
                }
            }
            for(int i=1; i<n; i++){
                R = prod(R, A);
            }
            return R;
        }
    }
    public static double[][] prod(double A[][], double B[]){
        double X[][] = new double[B.length][1];
        for(int i=0; i<B.length; i++){
            X[i][0] = B[i];
        }
        return prod(A, X);
    }
    public static double[][] prod(double A[][], double a){
        double R[][] = new double[A.length][A[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = A[i][j]*a;
            }
        }
        return R;
    }
    public static double[][] prod(double A[][], double B[][]){
        double R[][] = new double[A.length][B[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = 0;
                for(int k=0; k<B.length; k++){
                    R[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return R;
    }
    public static double[][] prod(double A[][], double B[][], double C[][]){
        return prod(prod(A,B),C);
    }
    public static double[][] prod(double A[][], double B[][], double C[][], double D[][]){
        return prod(prod(prod(A,B),C),D);
    }
    public static double[][] sum(double A[][], double B[][]){
        double R[][] = new double[A.length][B[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = A[i][j] + B[i][j];
            }
        }
        return R;
    }
    public static double [][] trans(double A[][]){
        double R[][] = new double[A[0].length][A.length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = A[j][i];
            }
        }
        return R;
    }
    
}
