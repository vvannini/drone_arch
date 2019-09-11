/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import ProOF.utilities.uUtil;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jsc.distributions.Normal;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author marcio
 */
public class PPDCPInstance extends Instance{
    //ATENÇÃO AS MATRIZES ABAIXO SÃO MODIFICADAS COM BASE EM LEITURAS DE ARQUIVOS
    public final double A[][] = new double[][]{
        {1, 0.7869, 0,      0},
        {0, 0.6065, 0,      0},
        {0,      0, 1, 0.7869},
        {0,      0, 0, 0.6065}
    };
    public final double B[][] = new double[][]{
        {0.2131, 0     },
        {0.3935, 0     },
        {     0, 0.2131},
        {     0, 0.3935}
    };
    public final double invB[][] = new double[][]{
        {0.5/B[0][0],   0.5/B[1][0],    0,              0               },
        {0,             0,              0.5/B[2][1],    0.5/B[3][1]     },
    };
    public final double Q[][] = new double[][]{
        {0.003555, 0,      0,      0},
        {0, 0.006320,      0,      0},
        {0,      0, 0.003555,      0},
        {0,      0,      0, 0.006320}
    };
    public final double P0[][] = new double[][]{
        {0.05*0.05,      0,          0,          0},
        {0,     0.005*0.005,          0,          0},
        {0,          0,      0.05*0.05,          0},
        {0,          0,         0,      0.005*0.005}
    };

    public final double X0[] = new double[]{
        0, 0, 0, 0
    };
    public final double Xgoal[] = new double[]{
        0, 0, -10, 0
    };
    public int Identificador = 1;
    public final double M = 1e3;    //if M>0 -> Mjti=M and Ejti=M 
    public double Mjti[][][];
    public double Ejti[][][];
    
    public double VMAX = 3;
    //public double VMIN = 0;
    public double UMAX = 1;
    public double DELTA = 0.05;
    public double DT = 1;
    public int T = 10;
    //public int N = 32;  //polygon faces
    public double AA = 3.236049505;
    public double BB = -1.787009505;
    
    public final int DN = defineDN();
    
    private File file_inst;
    private File file_dinamic;
    
    //private Polygon poly[];
    //private Point2D poly[][];
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
    public Obstacle In[][] = null;
    public Obstacle U = null;
    
    public boolean online = false;

    private boolean colision(double x1, double y1) {
        for(int j=0; j<J; j++){
            if(Ob[j].poly.contains(x1, y1)){
                return true;
            }
        }
        return false;
    }
    private boolean colisionDy(int t, double x1, double y1) {
        for(int j=0; j<E; j++){
            if(Dy[j][t].poly.contains(x1, y1)){
                return true;
            }
        }
        return false;
    }
    private boolean outOfIn(int t, double x1, double y1) {
        for(int j=0; j<I; j++){
            if(!In[j][t].poly.contains(x1, y1)){
                return true;
            }
        }
        return false;
    }

    
    
    public class Obstacle{
        public final Point2D points[];
        public final Line lines[];
        public final double Cx;
        public final double Cy;
        public final double R;
        public Polygon poly;
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
                
                
                if(PRINT)System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  = %8.4f\n", ax, ay, b);
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
    
    public static final int NORM2_square = 0;
    public static final int NORM1 = 1;
    public static final int NORM2 = 2;
    public static final int NORM2_square_Num = 3;
    
    public static final int NORM2_precision = 32;
    
    private static final boolean INVERT_Y = false;//true;
    public static final boolean PLOT_NORM = false;
    public static final boolean OUTPUT = false;

    
    public int OBJ = NORM2_square_Num;
    
    
    public static final int REST_empty = 0;
    public static final int REST_new   = 1;
    public int REST = REST_empty;
    
    public static final double ALPHA_old = 1.0;
    public static final double ALPHA_better = 0.25;
    public double ALPHA = ALPHA_old;
    
    public static final boolean PRINT = false;
    public static boolean PLOT = false;
    private static final boolean TEMP_FIX = false;
    
    private int dX = 11;
    private String format = "%1.0f";
    private final int format_X = 1;
    private final int format_Y = 90;
    
    @Override
    public String name() {
        return "Instance-PPDCP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file_inst = win.File("Map for PPDCP",null,"sgl");
        file_dinamic = win.File("Dinamic for PPDCP",null,"sgl");
        OBJ = win.Itens("Objectives", 3, "SQRNorm2CPX", "Norm1", "Norm2Aprox32", "SQRNorm2Aprox32");
        REST = win.Itens("Extra Restrictions", 1, "Empty", "New");
//        int index = win.Itens("Alpha", 0, "1.0", "1/4");
//        ALPHA = index==0 ? 1.0 : 1.0/4;
        ALPHA = ALPHA_old;
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        win.writeDbl("inst-DELTA", DELTA);
        win.writeInt("inst-T", T);
        win.writeInt("inst-J", J);
    }
    
    
    private int width, heigth;//, cx, cy;
    private double zoon;
    private static int MAX = 2;
    public static double P_Delta = 1e9;
    //public static final double P2 = 25;
    public static double P_Goal = 100;
    public static double P_Obj = 1;
    private static boolean mode = false;
    
    private static final double offset = 0.2;
    
    @Override
    public void load() throws FileNotFoundException, IOException, JDOMException {
        
        Scanner in = new Scanner(file_dinamic);
        
        double SD = 0.006;
        double TEMPO = 20;
        
        System.out.println(in.nextLine());
        TEMPO = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        SD = Double.parseDouble(in.nextLine());
       
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            X0[i] = Double.parseDouble(in.next());
        }
        in.nextLine();
        
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            Xgoal[i] = Double.parseDouble(in.next());
        }
        in.nextLine();
        
        System.out.println(in.nextLine());
        VMAX = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        UMAX = Double.parseDouble(in.nextLine());
        
        //System.out.println(in.nextLine());
        //VMIN = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        DELTA = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        T = Integer.parseInt(in.nextLine());
        DT = TEMPO/T;
        //T=15;
        System.out.println(in.nextLine());
        P_Obj = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        P_Goal = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        P_Delta = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        width = Integer.parseInt(in.next());
        heigth = Integer.parseInt(in.next());
        in.nextLine();
        
        System.out.println(in.nextLine());
        MAX = Integer.parseInt(in.next());
        System.out.println("MAX = "+MAX);
        in.nextLine();
        
        System.out.println(in.nextLine());
        mode = Boolean.parseBoolean(in.nextLine());
        System.out.println("MODE = "+mode);
        
        System.out.println(in.nextLine());
        online = Boolean.parseBoolean(in.nextLine());
        System.out.println("ONLINE = "+online);
        
        if(online){
            System.out.println(in.nextLine());
            PLOT = Boolean.parseBoolean(in.nextLine());
            System.out.println("PLOT = "+PLOT);
        }
        
        in.close();
        
        boolean read_file_inst = true;
        
        if(online){
//------------ vel angulo da aeronave------------
//1.0 0.75
//----------- time horizon (K, seconds) ---------
//20
//------------------ waypoints ------------------
//20
//------------------- destine -------------------
//0	-10
            in = new Scanner(new File("./config/config-dynamic.txt"));
            System.out.println(in.nextLine());
            double vel = Double.parseDouble(in.next());
            double ang = Double.parseDouble(in.nextLine());
            System.out.println(in.nextLine());
            TEMPO = Double.parseDouble(in.nextLine());
            System.out.println(in.nextLine());
            T = Integer.parseInt(in.nextLine());
            DT = TEMPO/T;
            System.out.println(in.nextLine());
            Xgoal[0] = Double.parseDouble(in.next());
            Xgoal[2] = Double.parseDouble(in.nextLine());
            in.close();
            
            in = new Scanner(new File("./config/config-intruders.txt"));
            System.out.println(in.nextLine());
            E = Integer.parseInt(in.nextLine());
            Dy = new Obstacle[E][T+1];
            for(int e=0; e<E; e++){
                double px = Double.parseDouble(in.next());
                double py = Double.parseDouble(in.next());
                double e_vel = Double.parseDouble(in.next());
                double e_ang = Double.parseDouble(in.next());
                for(int t=0; t<T+1; t++){
                    Dy[e][t] = new Obstacle(create(
                            px + Math.cos(e_ang)*e_vel*t*DT,
                            py + Math.sin(e_ang)*e_vel*t*DT, Math.sqrt(2*SD*SD)*Normal.inverseStandardCdf(1-2*DELTA), 0.0));
                }
            }
            in.close();
            
            X0[1] = Math.cos(ang)*vel;
            X0[3] = Math.sin(ang)*vel;
            /*
            //------------------------------ XML -------------------------------------------------
            File file_env = new File("./config/env.xml");
            if(file_env.exists()){
                read_file_inst = false;
            }
            
            minX = Double.POSITIVE_INFINITY;
            minY = Double.POSITIVE_INFINITY;
            maxX = Double.NEGATIVE_INFINITY;
            maxY = Double.NEGATIVE_INFINITY;
            
            SAXBuilder builder = new SAXBuilder();
            Document doc; 
            
            doc = builder.build(file_env);
            Element eEnviroment = doc.getRootElement();
            
            LinkedList<Obstacle> listObsctacles = new LinkedList<Obstacle>();
            List<Element> eListObstacle = eEnviroment.getChildren("obstacle");
            for(Element eObstacle : eListObstacle){
                LinkedList<Point2D> listPoints = new LinkedList<Point2D>();
                List<Element> eListCorner = eObstacle.getChild("shape").getChild("polygon").getChildren("corner");
                for(Element eCorner : eListCorner){
                    Point2D corner = new Point2D.Double(
                        Double.parseDouble(eCorner.getChildText("x")),
                        Double.parseDouble(eCorner.getChildText("y"))
                    );
                    listPoints.addLast(corner);
                    minX = Math.min(minX, corner.getX());
                    minY = Math.min(minY, corner.getY());
                    maxX = Math.max(maxX, corner.getX());
                    maxY = Math.max(maxY, corner.getY());
                    System.out.println("corner: "+eCorner +" : "+ corner.toString());
                }
                listObsctacles.add(new Obstacle(listPoints.toArray(new Point2D[listPoints.size()])));
            }
            
            J = listObsctacles.size();
            Ob = listObsctacles.toArray(new Obstacle[J]);
            
//            for(int i=0; i<4; i++){
//                X0[i] = Project3.Project3.X0[i];
//                Xgoal[i] = Project3.Project3.Xgoal[i];
//            }
            */
            minX = Math.min(X0[0], Xgoal[0]);
            minY = Math.min(X0[2], Xgoal[2]);
            maxX = Math.max(X0[0], Xgoal[0]);
            maxY = Math.max(X0[2], Xgoal[2]);
            
        }
        
        
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                A[i][j] = 0;
                Q[i][j] = 0;
                P0[i][j] = 0;
            }
            for(int j=0; j<2; j++){
                B[i][j] = 0;
            }
        }
        
        for(int i=0; i<4; i++){
            Q[i][i] = online ? 0 : DT*Math.pow(SD/20, 2);
            P0[i][i] = i%2==0 ? Math.pow(SD, 2) : 0;
        }
        for(int i=0; i<4; i++){
            A[i][i] = 1;
        }
        A[0][1] = DT;
        A[2][3] = DT;
        B[0][0] = DT*DT/2;
        B[1][0] = DT;
        B[2][1] = DT*DT/2;
        B[3][1] = DT;
        VMAX = VMAX * DT;
        UMAX = UMAX * DT * DT;
        
        
        double x1 = 1-2*DELTA;
        double x2 = 1;
        double y1 = erf_inv(x1);
        double y2 = erf_inv(x2);
        AA = (y2-y1)/(x2-x1);
        BB = (y1*x2-y2*x1)/(x2-x1);
        
        if(PRINT)System.out.println("DELTA = "+DELTA);
        if(PRINT)System.out.println("T     = "+T);
        if(PRINT)System.out.println("AA    = "+AA);
        if(PRINT)System.out.println("BB    = "+BB);
        
        System.out.println("AA    = "+AA);
        System.out.println("BB    = "+BB);
        
        if(read_file_inst){
            Scanner sc = new Scanner(file_inst);
            sc.nextLine();
            J = Integer.parseInt(sc.nextLine());
            //J = 0;
            //poly = new Polygon[J];

            minX = Math.min(X0[0], Xgoal[0]);
            minY = Math.min(X0[2], Xgoal[2]);
            maxX = Math.max(X0[0], Xgoal[0]);
            maxY = Math.max(X0[2], Xgoal[2]);

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
            sc.close();
        }
       
        
        if(TEMP_FIX){
            minX = -6;
            maxX = 6;
            minY = -10;
            maxY = 0;
        }
        
        double temp = Math.max(maxX-minX, maxY-minY);
        minX -= temp*offset;
        minY -= temp*offset;
        maxX += temp*offset;
        maxY += temp*offset;
        
        
//        Dy = new Obstacle[E][T+1];
//        int div = 4;
//        for(int t=0; t<T+1; t++){
//            if(E>0){
//            //Dy[0][t] = new Obstacle(create(maxX-t*(maxX-minX)/(T)-1, (minY+maxY)/2, (T*2.0)/(T+Math.abs(2*t-T)), (t*2*Math.PI)/T));
//            //Dy[0][t] = new Obstacle(create(maxX-(t%div)*(maxX-minX)/(div-1), (minY+maxY)/2, (T*2.0)/(T+Math.abs(2*t-T)), (t*2*Math.PI)/T));
//            //Dy[0][t] = new Obstacle(create(maxX-(t%div)*(maxX-minX)/(div-1), (minY+maxY)/2, ((t+T))/(T*1.0), (t*2*Math.PI)/T));
//            
////            Dy[0][t] = new Obstacle(create(-4, maxY-t*(maxY-minY)/(T), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
////            Dy[1][t] = new Obstacle(create(+4, maxY-t*(maxY-minY)/(T), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
//            
////            Dy[0][t] = new Obstacle(create(-5, 0+minY+t*(maxY-minY)/(T*1.0), ((t+T))/(T*3.0), 0.0));
////            Dy[1][t] = new Obstacle(create(+4, 4+minY+t*(maxY-minY)/(T*1.5), ((t+T))/(T*1.0), (t*2*Math.PI)/T));
////            
////            Dy[0][t] = new Obstacle(create(5, -4+maxY-t*(maxY-minY)/(T*3), ((t+T))/(T*3.0), 0.0));
////            Dy[1][t] = new Obstacle(create(-3+maxX-((t+5)%div)*(maxX-minX)/(div*1.3-1), -3, ((t+T))/(T*1.5), 0.0));
//              Dy[0][t] = new Obstacle(create(-4, 5+minY+t*(maxY-minY)/(T*2.0), ((t+T))/(T*2.0), 0.0));
//              Dy[1][t] = new Obstacle(create(-2.0+maxX-(t)*(maxX-minX)/(T*1.7-1), -7+maxY-t*(maxY-minY)/(T*3.0), ((t+T))/(T*2.0), 0.0));
//              Dy[2][t] = new Obstacle(create(-6+maxX-(t)*(maxX-minX)/(T*6.6-1), 8+minY+t*(maxY-minY)/(T*4.0), ((t+T))/(T*2.0), 0.0));
//            }
//        }
//        for(int i=0; i<4; i++){
//            Xgoal[i] = X0[i];
//        }
        
        In = new Obstacle[I][T+1];
        for(int t=0; t<T+1; t++){
            if(I>0){
                if(t==1+T/2){
                    In[0][t] = new Obstacle(create(-3, -8, 1, 0.0));
                }
            }
        }
        
        sum_t = new double[T+1][4][4];
        
        for(int t=0; t<T+1; t++){
            sum_t[t] = prod(pow(A,t), P0, pow(trans(A), t));
            for(int i=0; i<t; i++){
                sum_t[t] = sum(sum_t[t], prod(pow(A,i), Q, pow(trans(A),i)));
            }
        }
        
        sum_Ut = new double[T][2][2];
        for(int t=0; t<T; t++){
            sum_Ut[t][0][0] = Math.pow(SD, 2);
            sum_Ut[t][1][1] = Math.pow(SD, 2);
        }
        //U = new Obstacle(create(0, 0, UMAX, 0.0));
        
        
        if(PRINT){
            for(int t=0; t<T+1; t++){
                System.out.printf("-------------- sum[%d] ------------\n", t);
                for(int i=0; i<4; i++){
                    for(int j=0; j<4; j++){
                        double v = sum_t[t][i][j];
                        
                        if(Math.abs(v)<0.0001){
                            System.out.printf(Locale.ENGLISH, "%8s ", ".");
                        }else{
                            System.out.printf(Locale.ENGLISH, "%8.4f ", v);
                        }
                    }
                    System.out.println();
                }
            }
        }
        
        /*
        aji = new double[J][][];
        bji = new double[J][];
        for(int j=0; j<J; j++){
            aji[j] = new double[poly[j].length][4];
            bji[j] = new double[poly[j].length];
            
            for(int i=0; i<poly[j].length; i++){
                int k = (i+1) % poly[j].length;
                x1 = poly[j][i].getX();
                y1 = poly[j][i].getY();
                x2 = poly[j][k].getX();
                y2 = poly[j][k].getY();
                
                //x'
                aji[j][i][0] = -( y2 - y1 );
                //y'
                aji[j][i][2] = +( x2 - x1 );
                
                bji[j][i] = aji[j][i][0] * x1  + aji[j][i][2] * y1;
                
                if(PRINT)System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  = %8.4f\n", aji[j][i][0], aji[j][i][2], bji[j][i]);
            }
        }
        
        
        if(PRINT){
            for(int j=0; j<poly.length; j++){
                System.out.println("---------- poly["+j+"]------------");
                for(int i=0; i<poly[j].length; i++){
                    int k = (i+1) % poly[j].length;
                    System.out.printf("(%4g,%4g) -> (%4g,%4g)\n", poly[j][i].getX(), poly[j][i].getY(), poly[j][k].getX(), poly[j][k].getY());
                }
            }
        }
        
        
        Rjti = new double[J][T+1][];
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- Rj["+j+"]------------");
            for(int t=0; t<T+1; t++){
                Rjti[j][t] = new double[bji[j].length];
                for(int i=0; i<bji[j].length; i++){
                    double R[][] = prod(sum_t[t], aji[j][i]);
                    
                    double prod = 0;
                    for(int k=0; k<4; k++){
                        prod += aji[j][i][k] * R[k][0]; 
                    }
                    Rjti[j][t][i] = Math.sqrt(2*prod);
                    //Rjti[j][t][i] = 0;
                    if(t==0){
                        if(PRINT)System.out.printf("%4g ", Rjti[j][t][i]);
                    }
                }
            }
            if(PRINT)System.out.println();
        }
        */
        Rjti = new double[J][T+1][];
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- Rj["+j+"]------------");
            for(int t=0; t<T+1; t++){
                Rjti[j][t] = new double[Ob[j].length()];
                for(int i=0; i<Ob[j].length(); i++){
                    Rjti[j][t][i] = Ob[j].R(i, sum_t[t]);
                    if(t==0){
                        if(PRINT)System.out.printf("%4g ", Rjti[j][t][i]);
                    }
                }
            }
            if(PRINT)System.out.println();
        }
        
        LinkedList<Point2D> points[] = new LinkedList[T+1];
        for(int t=0; t<T+1; t++){
            points[t] = new LinkedList<Point2D>();
            points[t].add(new Point2D.Double(X0[0], X0[2]));
            points[t].add(new Point2D.Double(Xgoal[0], Xgoal[2]));
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
        
        plot("Start",null);
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
    public final static int defineDN(){
        int dm = 9;//11;//7 - (int)(Math.log10(inst.DELTA));
        //27 -- 0.1         -- 28-1     
        //24 -- 0.01        -- 26-2     //-2
        //18 -- 0.001       -- 21-3     //-5
        //7  -- 0.0001      -- 11-4     //10
        //
        System.out.println("dm = "+dm);
        final int DN = dm < 2 ? 2 : dm > 32 ? 32 : dm;   //DM in [2 ... 32]
        return DN;
    }
    public double DELTA(double d) throws Exception{
        return d*P_Delta;
    }
    public double GOAL(double d){
        return d*P_Goal;
    }
    public double OBJ(double d) throws Exception{
        return d*P_Obj;
    }
//    public double chi(int j, int t, int i){
//        return -bji[j][i] - (AA+BB) * Rjti[j][t][i]; 
//    }
//    public double psi(int j, int t, int i){
//        return -2*AA*(Rjti[j][t][i]+1e-9);
//    }
    
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
    
    
    
    private JFrame frame;
    private JPanel draw;
    private ArrayList<double[][]> listMU = new ArrayList<double[][]>();
    private ArrayList<double[][]> listU = new ArrayList<double[][]>();
    private ArrayList<double[]> listD = new ArrayList<double[]>();
    private ArrayList<int[][][]> listZ = new ArrayList<int[][][]>();
    
    
    public void clear(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listMU.clear();
                listU.clear();
            }
        }); 
    }
    private int tt = -1;
    private int t_plot = -1;
    private double obj = -1;
    private double dist = -1;
    private double gap = -1;
    private double time = -1;
    private int log_btm = 1;
    public void plot(final double[][] vMt) {
        plot(vMt, dX, mode ? "%dº%d'%d\"%c" : format);
    }
    public void plot(final String title, final double[][] vMt) {
        plot(title, vMt, dX, mode ? "%dº%d'%d\"%c" : format);
    }
    public void plot(final String title, final double[][] vMt, final double[] vDt) {
        plot(title, vMt, vDt, dX, mode ? "%dº%d'%d\"%c" : format);
    }
    public void plot(final String title, final double[][] vMt, final double[] vDt,
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, vDt, dX, mode ? "%dº%d'%d\"%c" : format, obj, dist, gap, time);
    }
    public void plot(final double[][] vMt, final int dX, final String format) {
        plot("title", vMt, dX, format);
    }
    public void plot(final String title, final double[][] vMt, final double[][] vUt) {
        plot(title, vMt, vUt, dX, mode ? "%dº%d'%d\"%c" : format);
    }
    public void plot(final String title, final double[][] vMt, final double[][] vUt, final double[] vDt) {
        plot(title, vMt, vUt, vDt, dX, mode ? "%dº%d'%d\"%c" : format);
    }
    public void plot(final String title, final double[][] vMt, final double[][] vUt, final double[] vDt,
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, vUt, vDt, dX, mode ? "%dº%d'%d\"%c" : format, obj, dist, gap, time);
    }
    public void plot(final double[][] vMt, final double[][] vUt, final int dX, final String format) {
        plot("title", vMt, vUt, dX, format);
    }
    public void plot(String title, final double[][] vMt, final int dX, final String format) {
        plot(title, vMt, null,null, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[] vDt, final int dX, final String format) {
        plot(title, vMt, null,vDt, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[] vDt, final int dX, final String format, 
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, null,vDt, dX, format, null, -1, obj, dist, gap, time);
    }
    public void plot(final String title, final double[][] vMt, final int[][][] Zjti, final int t) {
        plot(title, vMt, null, null, dX, mode ? "%dº%d'%d\"%c" : format, Zjti, t, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[][] vUt, final int dX, final String format) {
        plot(title, vMt, vUt, null, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[][] vUt, final double[] vDt, final int dX, final String format) {
        plot(title, vMt, vUt, vDt, dX, format, null, -1, -1, -1, -1, -1);
    }
    public void plot(String title, final double[][] vMt, final double[][] vUt, final double[] vDt, final int dX, final String format, 
            final double obj, final double dist, final double gap, final double time) {
        plot(title, vMt, vUt, vDt, dX, format, null, -1, obj, dist, gap, time);
    }
    public void plot(final String title, final double[][] vMt, final double[][] vUt, final int[][][] Zjti, final int t) {
        plot(title, vMt, vUt, null, dX, mode ? "%dº%d'%d\"%c" : format, Zjti, t, -1, -1, -1, -1);
    }
    
    
    public void plot(
        final String title, final double[][] vMt, final double[][] vUt, final double[] vDt, 
        final int dX, final String format, final int[][][] Zjti, final int t,
        final double obj, final double dist, final double gap, final double time
    ) {
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
        if(vUt!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listU.clear();
                    listU.add(vUt);
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
                        if(mode){
                            pintar_geographical(g);
                        }else{
                            pintar_cartesian(g, log_btm);
                        }
                    }
                };
                draw.setPreferredSize(new Dimension(width, heigth));
                JButton btm = new JButton("next");
                btm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        t_plot = (t_plot+1)%(T+1);
                        draw.repaint();
                    }
                });
                btm.setSize(new Dimension(120, 23));
                final JButton btm2 = new JButton("Risk Alocation");
                btm2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(log_btm==0){
                            log_btm++;
                            btm2.setText("Risk Alocation");
                        }else if(log_btm==1){
                            log_btm++;
                            btm2.setText("Particle Control");
                        }else if(log_btm==2){
                            log_btm = 0;
                            btm2.setText("Fixed Risk Relaxion");
                        }
                        draw.repaint();
                    }
                });
                btm2.setSize(new Dimension(120, 23));
                frame.setLayout(new FlowLayout());
                frame.add(btm);
                frame.add(btm2);
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
        save(name, -1, false);
    }
    public void save(String name, int t_plot, boolean name_simple){
        BufferedImage img = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        
        this.t_plot = t_plot;
        
        try {
            if(mode){
                pintar_geographical(g);
            }else{
                pintar_cartesian(g, 0);
            }
            
            //System.out.println("save in ./sol_"+name+".png");
            //ImageIO.write(img,   "png", new File("C:/zPNG/sol_"+Identificador+"_"+name+".png"));
            if(name_simple){
                ImageIO.write(img,   "png", new File("./sol_"+job.getName()+"_"+name+".png"));
            }else{
                ImageIO.write(img,   "png", new File("./sol_D"+DELTA+"_T"+T+"_J"+J+"_OBJ"+OBJ+"_"+job.getName()+"_"+Identificador+"_"+name+".png"));
            }
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(!TEMP_FIX)JOptionPane.showMessageDialog(frame, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
        try {
            if(mode){
                pintar_geographical(g);
            }else{
                pintar_cartesian(g, 1);
            }
            
            //System.out.println("save in ./sol_"+name+".png");
            //ImageIO.write(img,   "png", new File("C:/zPNG/sol_"+Identificador+"_"+name+".png"));
            if(name_simple){
                ImageIO.write(img,   "png", new File("./log_"+job.getName()+"_"+name+".png"));
            }else{
                ImageIO.write(img,   "png", new File("./log_D"+DELTA+"_T"+T+"_J"+J+"_OBJ"+OBJ+"_"+job.getName()+"_"+Identificador+"_"+name+".png"));
            }
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(!TEMP_FIX)JOptionPane.showMessageDialog(frame, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
        try {
            if(mode){
                pintar_geographical(g);
            }else{
                pintar_cartesian(g, 2);
            }
            
            //System.out.println("save in ./sol_"+name+".png");
            //ImageIO.write(img,   "png", new File("C:/zPNG/sol_"+Identificador+"_"+name+".png"));
            if(name_simple){
                ImageIO.write(img,   "png", new File("./ctr_"+job.getName()+"_"+name+".png"));
            }else{
                ImageIO.write(img,   "png", new File("./ctr_D"+DELTA+"_T"+T+"_J"+J+"_OBJ"+OBJ+"_"+job.getName()+"_"+Identificador+"_"+name+".png"));
            }
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(!TEMP_FIX)JOptionPane.showMessageDialog(frame, ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    
    
    
    public void pintar_geographical(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, width-1, heigth-1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width-1, heigth-1);
        //g2.translate(cx, cy);
        //g2.scale(zoon, zoon);
    
        final int off = 120;
        zoon = Math.min((width-off-5)/(maxX-minX), (heigth-off-5)/(maxY-minY));
        
        g2.setColor(Color.WHITE);
        g2.fillRect(off, off, (int)(zoon*(maxX-minX)), (int)(zoon*(maxY-minY)));
        g2.setColor(Color.BLACK);
        g2.drawRect(off, off, (int)(zoon*(maxX-minX)), (int)(zoon*(maxY-minY)));
        
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        
        double step =  Math.max(maxX-minX, maxY-minY)*3600/6;
        if(step>3600){
            step = (int)(step/3600) * 3600;
        }else if(step>600){
            step = (int)(step/600) * 600;
        }else if(step>60){
            step = (int)(step/60) * 60;
        }else if(step>10){
            step = (int)(step/10) * 10;
        }
        g2.drawString(String.format("step = %1.0f\"", step), 10, 15);
        //double val = ((int)((minX*3600)/step))*step/3600;
        int val = (int)(((int)((minX*3600)/step))*step);
        val += minX < 0 ? -step : step;
        while(val<minX*3600){
            val += step;
        }
        while(val<maxX*3600){
            int degrees = Math.abs(val)/3600;
            int min = ( Math.abs(val)-degrees*3600)/60;
            int sec = ( Math.abs(val)-degrees*3600-min*60);
            int x = (int)(zoon*(val/3600.0-minX)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%dº%d'%d\"%c", degrees, min, sec, val>0?'L':'O'), x-40, off-10);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, off, x, off+(int)(zoon*(maxY-minY)));
            
            val += step;
        }
        val = (int)(((int)((minY*3600)/step))*step);
        val += minY < 0 ? -step : step;
        while(val<minY*3600){
            val += step;
        }
        while(val<maxY*3600){
            int degrees = Math.abs(val)/3600;
            int min = (Math.abs(val)-degrees*3600)/60;
            int sec = (Math.abs(val)-degrees*3600-min*60);
            int y = (int)(zoon*(val/3600.0-minY)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%dº%d'%d\"%c", degrees, min, sec, val>0?'S':'N'), 10, y+4);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(off, y, off+(int)(zoon*(maxX-minX)), y);
            
            val += step;
        }
        
        //g2.drawRect(50, 50, 400, 300);
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- poly["+j+"]------------");
            for(int i=0; i<Ob[j].length(); i++){
                int k = (i+1) % Ob[j].length();
                if(PRINT)System.out.printf("(%4g,%4g) -> (%4g,%4g)\n", Ob[j].points[i].getX(), Ob[j].points[i].getY(), Ob[j].points[k].getX(), Ob[j].points[k].getY());
            }
            if(PRINT)System.out.println("---------- lines["+j+"]------------");
            for(int i=0; i<Ob[j].length(); i++){
                double x1, y1;
                if(Math.abs(Ob[j].lines[i].ay)>0.01){
                    x1 = Ob[j].points[i].getX();
                    y1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ax * x1 ) / Ob[j].lines[i].ay;
                }else{
                    y1 = Ob[j].points[i].getY();
                    x1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ay * y1 ) / Ob[j].lines[i].ax;
                }


                int k = (i+1) % Ob[j].length();

                double x2, y2;
                if(Math.abs(Ob[j].lines[k].ay)>0.01){//if(Math.abs(O[j].lines[i].ay)>0.01){
                    x2 = Ob[j].points[k].getX();
                    y2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ax * x2 ) / Ob[j].lines[k].ay;
                }else{
                    y2 = Ob[j].points[k].getY();
                    x2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ay * y2 ) / Ob[j].lines[k].ax;
                }


                double S = step/(Math.sqrt(Ob[j].lines[i].ax*Ob[j].lines[i].ax + Ob[j].lines[i].ay*Ob[j].lines[i].ay) * 18000);
                if(PRINT)System.out.printf("(%4d,%4d) -> (%4d,%4d)\n", (int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                g2.setColor(Color.BLACK);
                g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                //g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                //g2.drawPolygon(poly[j]);
                g2.setColor(Color.BLUE);
                g2.drawLine((int)(zoon*((x1+x2)/2-minX))+off, (int)(zoon*((y1+y2)/2-minY))+off, (int)(zoon*((x1+x2)/2-minX+S*Ob[j].lines[i].ax))+off, (int)(zoon*((y1+y2)/2-minY+S*Ob[j].lines[i].ay))+off);
                g2.fillOval((int)(zoon*((x1+x2)/2-minX))+off-5, (int)(zoon*((y1+y2)/2-minY))+off-5, 10, 10);
                g2.fillOval((int)(zoon*((x1+x2)/2-minX+S*Ob[j].lines[i].ax))+off-5, (int)(zoon*((y1+y2)/2-minY+S*Ob[j].lines[i].ay))+off-5, 10, 10);
            }
            double xm = 0;
            double ym = 0;
            for(int i=0; i<Ob[j].length(); i++){
                xm += Ob[j].points[i].getX();
                ym += Ob[j].points[i].getY();
            }
            xm = xm / Ob[j].length();
            ym = ym / Ob[j].length();
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("[%d]", j), (int)(zoon*(xm-minX)+off)-20, (int)(zoon*(ym-minY)+off)+5);
        }
        
        g2.setStroke(new BasicStroke(3)); 
        for(int Zjti[][][] : listZ){
            int t = tt;
            g2.setColor(Color.BLUE);
            for(int j=0; j<J; j++){
                for(int i : Zjti[j][t]){
                    double x1, y1;
                    if(Math.abs(Ob[j].lines[i].ay)>0.01){
                        x1 = Ob[j].points[i].getX();
                        y1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ax * x1 ) / Ob[j].lines[i].ay;
                    }else{
                        y1 = Ob[j].points[i].getY();
                        x1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ay * y1 ) / Ob[j].lines[i].ax;
                    }


                    int k = (i+1) % Ob[j].length();

                    double x2, y2;
                    if(Math.abs(Ob[j].lines[k].ay)>0.01){//if(Math.abs(O[j].lines[i].ay)>0.01){
                        x2 = Ob[j].points[k].getX();
                        y2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ax * x2 ) / Ob[j].lines[k].ay;
                    }else{
                        y2 = Ob[j].points[k].getY();
                        x2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ay * y2 ) / Ob[j].lines[k].ax;
                    }
                    g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
        g2.fillRect((int)(-10+(int)(zoon*(Xgoal[0]-minX)+off)), (int)(-10+(int)(zoon*(Xgoal[2]-minY)+off)), 20, 20);
        g2.fillOval((int)(-10+(int)(zoon*(X0[0]-minX)+off)), (int)(-10+(int)(zoon*(X0[2]-minY)+off)), 20, 20);
        g2.drawRect(-800, -1100, 1400, 1100);

        for(double [][] vMUt : listMU){
            g2.setColor(Color.red);
            if(tt==-1){
                for(int t=0; t<T; t++){
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];
                    double x2 = vMUt[t+1][0];
                    double y2 = vMUt[t+1][2];
                    g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                    g2.fillOval((int)(zoon*(x1-minX)+off-5), (int)(zoon*(y1-minY)+off-5), 10, 10);
                    g2.fillOval((int)(zoon*(x2-minX)+off-5), (int)(zoon*(y2-minY)+off-5), 10, 10);
                }
                for(int t=0; t<T+1; t++){
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];

                    double rx = 0;
                    double ry = 0;
                    for(int j=0; j<4; j++){
                        rx += sum_t[t][0][j];
                        ry += sum_t[t][2][j];
                    }
                    double cx = Math.sqrt(2*rx)*erf_inv(1-2*DELTA);
                    double cy = Math.sqrt(2*ry)*erf_inv(1-2*DELTA);

                    //double cx = erf_inv(1-2*DELTA);
                    //double cy = erf_inv(1-2*DELTA);

                    if(PRINT)System.out.println("Cx = "+cx);
                    if(PRINT)System.out.println("Cy = "+cy);
                    g2.drawOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                    
                    
                    //g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                    //g2.drawOval((int)(zoon*(x1-cx-minX)+off), (int)(zoon*(y1-minY)+off-5), 10, 10);
                    //g2.fillOval((int)(zoon*(x2-minX)+off-5), (int)(zoon*(y2-minY)+off-5), 10, 10);
                    
                    //g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
                }
            }else{
                int t = tt;
                double x1 = vMUt[t][0];
                double y1 = vMUt[t][2];

                double rx = 0;
                double ry = 0;
                for(int j=0; j<4; j++){
                    rx += sum_t[t][0][j];
                    ry += sum_t[t][2][j];
                }
                double cx = Math.sqrt(2*rx)*erf_inv(1-2*DELTA);
                double cy = Math.sqrt(2*ry)*erf_inv(1-2*DELTA);

                //double cx = erf_inv(1-2*DELTA);
                //double cy = erf_inv(1-2*DELTA);

                if(PRINT)System.out.println("Cx = "+cx);
                if(PRINT)System.out.println("Cy = "+cy);
                g2.drawOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*((2*cx))+off), (int)(zoon*((2*cy))+off));
                //g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
            }
                
        }
    }
    
    
    private Polygon create(double off, Point2D... poly){
        int npoints = poly.length;
        int xpoints[] = new int[npoints];
        int ypoints[] = new int[npoints];
        for(int i=0; i<poly.length; i++){
            xpoints[i] = (int)(zoon*(poly[i].getX()-minX)+off);
            ypoints[i] = (int)(zoon*(poly[i].getY()-minY)+off);
        }
        return new Polygon(xpoints, ypoints, npoints);
    }
    private void pintar_cartesian(Graphics g, final int log) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, width-1, heigth-1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width-1, heigth-1);
        //g2.translate(cx, cy);
        //g2.scale(zoon, zoon);
    
        final int off = 120;
        zoon = Math.min((width-off-5)/(maxX-minX), (heigth-off-5)/(maxY-minY));
        /*if(TEMP_FIX){
            zoon = 30;
        }*/
        
        g2.setColor(Color.WHITE);
        g2.fillRect(off, off, (int)(zoon*(maxX-minX)), (int)(zoon*(maxY-minY)));
        g2.setColor(Color.BLACK);
        g2.drawRect(off, off, (int)(zoon*(maxX-minX)), (int)(zoon*(maxY-minY)));
        
         
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        
        
        double step =  Math.max(maxX-minX, maxY-minY)*1000*2/(15-1);
        
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
        
        g2.drawString(String.format("step = %1.1f  | [sum(delta)=%g] <= [DELTA=%g] | zoom = %f", step, Delta_CA, DELTA, zoon), 10, 15);
        
        g2.drawString(String.format("obj = %12g  |  dist = %12g | gap = %12g | time = %12g", obj, dist, gap, time), 10, 35);
        //double val = ((int)((minX*3600)/step))*step/3600;
        int val = (int)(((int)((minX*3600)/step))*step);
        val += minX < 0 ? -step : step;
        while(val<minX*1000){
            val += step;
        }
        while(val<maxX*1000){
            double degrees = val/1000;
            int x = (int)(zoon*(val/1000.0-minX)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(format, degrees), x-format_X, off-10);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, off, x, off+(int)(zoon*(maxY-minY)));
            
            val += step;
        }
        val = (int)(((int)((minY*1000)/step))*step);
        val += minY < 0 ? -step : step;
        while(val<minY*1000){
            val += step;
        }
        while(val<maxY*1000){
            double degrees = -val/1000.0;
            int y = (int)(zoon*(val/1000.0-minY)+off);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(format, degrees), format_Y, y+4);
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(off, y, off+(int)(zoon*(maxX-minX)), y);
            
            val += step;
        }
        
        if(INVERT_Y){
            g2.scale(1.0, -1.0);
            g2.translate(0, -(int)(zoon*(maxY-minY)+2*off));
        }
        g2.setColor(Color.BLACK);
        g2.fillRect(-10+(int)(zoon*(Xgoal[0]-minX)+off), -10+(int)(zoon*(Xgoal[2]-minY)+off), 20, 20);
        g2.fillOval(-10+(int)(zoon*(X0[0]-minX)+off),    -10+(int)(zoon*(X0[2]-minY)+off),    20, 20);
        g2.drawRect(-800, -1100, 1400, 1100);
        
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- poly["+j+"]------------");
            for(int i=0; i<Ob[j].length(); i++){
                int k = (i+1) % Ob[j].length();
                if(PRINT)System.out.printf("(%4g,%4g) -> (%4g,%4g)\n", Ob[j].points[i].getX(), Ob[j].points[i].getY(), Ob[j].points[k].getX(), Ob[j].points[k].getY());
            }
            if(PRINT)System.out.println("---------- lines["+j+"]------------");
            
            if(Ob[j].poly==null){
                Ob[j].poly = create(off, Ob[j].points);
            }
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillPolygon(Ob[j].poly);
            
            plot_line(g2, Ob[j], off, step, Color.BLACK, Color.BLUE);
        }
        for(int j=0; Dy!=null && j<Dy.length; j++){
            for(int t=0; t<T+1; t++){
                if(t==t_plot){
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.format("%2d", t_plot), 
                            (int)(zoon*((Dy[j][t].Cx-Dy[j][t].R/5)-minX)+off),
                            (int)(zoon*((Dy[j][t].Cy+Dy[j][t].R/10)-minY)+off));
                        
                    g2.setColor(new Color(255, 0, 0, 128));
                }else{
                    g2.setColor(new Color(255, 200, 0, 64));
                }
                g2.fillPolygon(create(off, Dy[j][t].points));
                plot_line(g2, Dy[j][t], off, step, Color.RED, Color.BLUE);
                
            }
        }
        for(int j=0; j<In.length; j++){
            for(int t=0; t<T+1; t++){
                if(In[j][t]!=null){
                    if(t==t_plot){
                        g2.setColor(Color.BLACK);
                        g2.drawString(String.format("%2d", t_plot), 
                            (int)(zoon*((In[j][t].Cx-In[j][t].R/5)-minX)+off),
                            (int)(zoon*((In[j][t].Cy+In[j][t].R/10)-minY)+off));
                        g2.setColor(new Color(0, 255, 0, 128));
                    }else{
                        g2.setColor(new Color(0, 255, 0, 64));
                    }
                    g2.fillPolygon(create(off, In[j][t].points));
                    plot_line(g2, In[j][t], off, step, Color.GREEN, Color.BLUE);
                }
            }
        }
        
        g2.setStroke(new BasicStroke(3)); 
        for(int Zjti[][][] : listZ){
            int t = tt;
            g2.setColor(Color.BLUE);
            for(int j=0; j<J; j++){
                for(int i : Zjti[j][t]){
                    plot_line(g2, j, i, off, step, Color.BLUE, Color.BLUE);
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
        
        int colisions = 0;
        int cout = 0;
        for(double [][] vUt : listU){
            if(log==2){
                double x[][] = new double[T+1][4];
                Normal w0[] = new Normal[4];
                for(int i=0; i<4; i++){
                    w0[i] = P0[i][i] < 1e-9 ? null : new Normal(0, Math.sqrt(P0[i][i]));
                }
                Normal wt[] = new Normal[4];
                for(int i=0; i<4; i++){
                    wt[i] = Q[i][i] < 1e-9 ? null : new Normal(0, Math.sqrt(Q[i][i]));
                }

                for(; cout<10000; cout++){
                    for(int i=0; i<4; i++){
                        x[0][i] = X0[i] + (w0[i]==null ? 0 : w0[i].random());
                    }
                    for(int t=0; t<T+1; t++){
                        for(int i=0; i<4 && t<T; i++){
                            x[t+1][i] = 0;
                            for(int j=0; j<4; j++){
                                x[t+1][i] += A[i][j] * x[t][j];
                            }
                            for(int j=0; j<2; j++){
                                x[t+1][i] += B[i][j] * vUt[t][j];
                            }
                            x[t+1][i] += (wt[i]==null ? 0 : wt[i].random());
                        }

                        double x1 = x[t][0];
                        double y1 = x[t][2];
                        int vx = (int)(zoon*(x1-minX)+off);
                        int vy = (int)(zoon*(y1-minY)+off);
                        if(colision(vx, vy)){//|| colisionDy(t, x1, y1) || outOfIn(t, x1, y1)
                            colisions++;
                            g2.setColor(Color.BLACK);
                            final int r = 3;
                            g2.fillOval(vx-r, vy-r, 2*r, 2*r);
                            break;
                        }else{
                            g2.setColor(Color.GRAY);
                            final int r = 1;
                            g2.fillOval(vx-r, vy-r, 2*r, 2*r);
                        }
                    }
                }
            }
        }
        g2.setColor(Color.BLACK);
        g2.drawString(String.format("control particles = %12d  |  colisions = %12d | risk = %12g\n", cout, colisions, colisions*1.0/cout), 10, 55);
        
        for(double [][] vMUt : listMU){
            double vDt[] = null;
            if(listD.size()>0){
                vDt = listD.get(0);
            }
            g2.setColor(Color.red);
            if(tt==-1){
                for(int t=0; t<T; t++){
                    final int r = 3;
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];
                    double x2 = vMUt[t+1][0];
                    double y2 = vMUt[t+1][2];
                    g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                    g2.fillOval((int)(zoon*(x1-minX)+off-r), (int)(zoon*(y1-minY)+off-r), 2*r, 2*r);
                    g2.fillOval((int)(zoon*(x2-minX)+off-r), (int)(zoon*(y2-minY)+off-r), 2*r, 2*r);
                    
                    //Polygon poly = create(off, U.points);
                    //poly.translate(-(int)(zoon*(x1-minX)+off), -(int)(zoon*(y1-minY)+off));
                    //g2.drawPolygon(poly);
                }
                for(int t=0; t<T+1; t++){
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];

                    double rx = 0;
                    double ry = 0;
                    for(int j=0; j<4; j++){
                        rx += sum_t[t][0][j];
                        ry += sum_t[t][2][j];
                    }
                    double cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*DELTA);
                    double cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*DELTA);

                    //double cx = erf_inv(1-2*DELTA);
                    //double cy = erf_inv(1-2*DELTA);

                    if(PRINT)System.out.println("Cx1 = "+cx);
                    if(PRINT)System.out.println("Cy1 = "+cy);
                    if(t==t_plot){
                        g2.setColor(Color.BLACK);
                        g2.drawString(String.format("%2d", t_plot), (int)(zoon*((x1+cx)-minX)+off), (int)(zoon*((y1)-minY)+off));
                        g2.setColor(new Color(255, 0, 0, 128));
                        g2.fillOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                    }
                    g2.setColor(Color.RED);
                    g2.drawOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                    
                    
                    if(vDt!=null && log>=1){
                        double delta = vDt[t];
                        cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*delta);
                        cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*delta);

                        //double cx = erf_inv(1-2*DELTA);
                        //double cy = erf_inv(1-2*DELTA);

                        if(PRINT)System.out.println("Cx2 = "+cx);
                        if(PRINT)System.out.println("Cy2 = "+cy);
                        if(t==t_plot){
                            g2.setColor(new Color(0, 0, 255, 128));
                            g2.fillOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                        }
                        g2.setColor(Color.BLUE);
                        g2.drawOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*2*cx), (int)(zoon*2*cy));
                        
                        if(PLOT_NORM){
                            g2.setColor(Color.BLACK);
                            g2.drawString(String.format("%g", vDt[t]),(int)(zoon*((x1+cx)-minX)+off), (int)(zoon*((y1)-minY)+off));
                        }
                    }
                    
                    
                    //g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
                    //g2.drawOval((int)(zoon*(x1-cx-minX)+off), (int)(zoon*(y1-minY)+off-5), 10, 10);
                    //g2.fillOval((int)(zoon*(x2-minX)+off-5), (int)(zoon*(y2-minY)+off-5), 10, 10);
                    
                    //g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
                }
            }else{
                int t = tt;
                double x1 = vMUt[t][0];
                double y1 = vMUt[t][2];

                double rx = 0;
                double ry = 0;
                for(int j=0; j<4; j++){
                    rx += sum_t[t][0][j];
                    ry += sum_t[t][2][j];
                }
                double cx = Math.sqrt(2*rx)*Normal.inverseStandardCdf(1-2*DELTA);
                double cy = Math.sqrt(2*ry)*Normal.inverseStandardCdf(1-2*DELTA);

                //double cx = erf_inv(1-2*DELTA);
                //double cy = erf_inv(1-2*DELTA);

                if(PRINT)System.out.println("Cx = "+cx);
                if(PRINT)System.out.println("Cy = "+cy);
                g2.drawOval((int)(zoon*((x1-cx)-minX)+off), (int)(zoon*((y1-cy)-minY)+off), (int)(zoon*((2*cx))+off), (int)(zoon*((2*cy))+off));
                //g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
            }
        }
        
        if(INVERT_Y){
            g2.translate(0.0, +(zoon*(maxY-minY)+2*off));
            g2.scale(1.0, -1.0);
        }
        for(int j=0; j<J; j++){
            g2.setColor(Color.BLUE);    
            //g2.drawString(String.format("[%d]", j), (int)(zoon*(xm-minX)+off)-20, (int)(zoon*(ym-minY)+off)+5);
            if(j>9){
                g2.drawString(String.format("%2d", j+1), (int)(zoon*(Ob[j].Cx-minX)+off)-10, (int)(zoon*(Ob[j].Cy-minY)+off)+5);
            }else{
                g2.drawString(String.format("%2d", j+1), (int)(zoon*(Ob[j].Cx-minX)+off)-15, (int)(zoon*(Ob[j].Cy-minY)+off)+5);
            }
        }
    }
    private void plot_line(Graphics2D g2, int j, int i, int off, double step, Color Color_line, Color Color_norm) {
        double x1, y1;
        if(Math.abs(Ob[j].lines[i].ay)>0.01){
            x1 = Ob[j].points[i].getX();
            y1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ax * x1 ) / Ob[j].lines[i].ay;
        }else{
            y1 = Ob[j].points[i].getY();
            x1 = ( Ob[j].lines[i].b - Ob[j].lines[i].ay * y1 ) / Ob[j].lines[i].ax;
        }
        int k = (i+1) % Ob[j].length();
        double x2, y2;
        if(Math.abs(Ob[j].lines[k].ay)>0.01){
            x2 = Ob[j].points[k].getX();
            y2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ax * x2 ) / Ob[j].lines[k].ay;
        }else{
            y2 = Ob[j].points[k].getY();
            x2 = ( Ob[j].lines[k].b - Ob[j].lines[k].ay * y2 ) / Ob[j].lines[k].ax;
        }
        g2.setColor(Color_line);
        g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
        //g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
        //g2.drawPolygon(poly[j]);
        if(PLOT_NORM){
            double S = step/(Ob[j].lines[i].norm() * 5000);
            g2.setColor(Color_norm);
            g2.drawLine((int)(zoon*((x1+x2)/2-minX))+off, (int)(zoon*((y1+y2)/2-minY))+off, (int)(zoon*((x1+x2)/2-minX+S*Ob[j].lines[i].ax))+off, (int)(zoon*((y1+y2)/2-minY+S*Ob[j].lines[i].ay))+off);
            g2.fillOval((int)(zoon*((x1+x2)/2-minX))+off-5, (int)(zoon*((y1+y2)/2-minY))+off-5, 10, 10);
            g2.fillOval((int)(zoon*((x1+x2)/2-minX+S*Ob[j].lines[i].ax))+off-5, (int)(zoon*((y1+y2)/2-minY+S*Ob[j].lines[i].ay))+off-5, 10, 10);
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
            g2.drawLine((int)(zoon*(x1-minX)+off), (int)(zoon*(y1-minY)+off), (int)(zoon*(x2-minX)+off), (int)(zoon*(y2-minY)+off));
            //g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
            //g2.drawPolygon(poly[j]);
            if(PLOT_NORM){
                double S = step/(poly.lines[i].norm() * 5000);
                g2.setColor(Color_norm);
                g2.drawLine((int)(zoon*((x1+x2)/2-minX))+off, (int)(zoon*((y1+y2)/2-minY))+off, (int)(zoon*((x1+x2)/2-minX+S*poly.lines[i].ax))+off, (int)(zoon*((y1+y2)/2-minY+S*poly.lines[i].ay))+off);
                g2.fillOval((int)(zoon*((x1+x2)/2-minX))+off-5, (int)(zoon*((y1+y2)/2-minY))+off-5, 10, 10);
                g2.fillOval((int)(zoon*((x1+x2)/2-minX+S*poly.lines[i].ax))+off-5, (int)(zoon*((y1+y2)/2-minY+S*poly.lines[i].ay))+off-5, 10, 10);
            }
        }
    }
    
    public static double dist2_position(double[] A, double[] B) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            if(i%2==0){
                r += (A[i]-B[i])*(A[i]-B[i]);
            }
        }
        return Math.sqrt(r);
    }
    public static double dist2(double[] A, double[] B) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += (A[i]-B[i])*(A[i]-B[i]);
        }
        return Math.sqrt(r);
    }
    public static double dist1(double[] A, double[] B) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += Math.abs(A[i]-B[i]);
        }
        return r;
    }
    public static double norm2(double[] A) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += (A[i])*(A[i]);
        }
        return Math.sqrt(r);
    }
    public static double norm1(double[] A) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += Math.abs(A[i]);
        }
        return r;
    }
    public static double energy2(double[] A) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += (A[i])*(A[i]);
        }
        return r;
    }
    
    
    /*
    private static double p(double x, double y){
        return Math.pow(x, y);
    }
    private static double erf_inv(double z){
        final double PI = Math.PI;
        return 0.5 * Math.sqrt(PI) * (z + PI*p(z,3)/12 + 7*p(PI,2)*p(z,5)/480 + 127*p(PI,3)*p(z,7)/40320 + 4369*p(PI,4)*p(z,9)/5806080 + 34807*p(PI,5)*p(z,11)/182476800);
    }*/
    
    private static double erf_inv(double z){
        double y = 0;
        double aux = 0;
        for(int k=MAX-1; k>=0; k--){
            aux = Ck(k)*Math.pow(z*Math.sqrt(Math.PI)/2, 2*k+1)/(2*k+1);
            y += aux;
        }
        return y;
    }
    
    private static int index = 1;
    
    private static double table[] = null;
    private static boolean flag[] = null;
    
    private static double Ck(int k){
        if(table==null){
            table = new double[MAX];
            flag = new boolean[MAX];
        }
        if(!flag[k]){
            double R = 0;
            if(k==0){
                R = 1;
            }else{
                for(int m=index; m<k; m++){
                    Ck(m);
                    index = m;
                }
                for(int m=0; m<k; m++){
                    R += (Ck(m)*Ck(k-1-m))/((m+1)*(2*m+1));
                }
            }
            
            flag[k] = true;
            
            table[k] = R;
        }
        return table[k];
    }

    public double XMAX() {
        return uUtil.dist(X0[0], Xgoal[0], X0[2], Xgoal[2]);
    }
    public double Rx(int t){
        double rx = 0;
        for(int j=0; j<4; j++){
            rx += sum_t[t][0][j];
        }
        return Math.sqrt(2*rx)*erf_inv(1-2*DELTA);
    }
    public double Ry(int t){
        double ry = 0;
        for(int j=0; j<4; j++){
            ry += sum_t[t][2][j];
        }
        return Math.sqrt(2*ry)*erf_inv(1-2*DELTA);
    }
    public Double[] X0() {
        return new Double[]{X0[0],X0[1],X0[2],X0[3]};
    }

    private static Polygon toPolygon(Point2D[] point2D){
        int npoints = point2D.length;
        int xpoints[] = new int[npoints];
        int ypoints[] = new int[npoints];
        
        for(int i=0; i<npoints; i++){
            xpoints[i] = (int) point2D[i].getX();
            ypoints[i] = (int) point2D[i].getY();
        }
        return new Polygon(xpoints, ypoints, npoints);
    }

    public int G(int j) {
        return Ob[j].length();
    }
    public int E(int j, int t) {
        return Dy[j][t].length();
    }
    public int I(int j, int t) {
        return In[j][t] == null ? 0 : In[j][t].length();
    }
}
