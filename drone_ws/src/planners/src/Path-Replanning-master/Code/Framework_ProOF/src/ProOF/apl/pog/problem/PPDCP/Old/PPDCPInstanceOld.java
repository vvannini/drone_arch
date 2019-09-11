/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP.Old;

import ProOF.com.Linker.LinkerParameters;
import ProOF.gen.stopping.Stop;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import ProOF.utilities.uUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author marcio
 */
public class PPDCPInstanceOld extends Instance{
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
    public final double Q[][] = new double[][]{
        {0.003555, 0,      0,      0},
        {0, 0.006320,      0,      0},
        {0,      0, 0.003555,      0},
        {0,      0,      0, 0.006320}
        
        /*
        {0.3555, 0,      0,      0},
        {0, 0.6320,      0,      0},
        {0,      0, 0.3555,      0},
        {0,      0,      0, 0.6320}
        */
        /*
        {0.3555*0.3555, 0,      0,      0},
        {0, 0.6320*0.6320,      0,      0},
        {0,      0, 0.3555*0.3555,      0},
        {0,      0,      0, 0.6320*0.6320}
             */
    };
    public final double P0[][] = new double[][]{
        {0.05*0.05,      0,          0,          0},
        {0,     0.005*0.005,          0,          0},
        {0,          0,      0.05*0.05,          0},
        {0,          0,         0,      0.005*0.005}
        /*
        {0.05,      0,          0,          0},
        {0,     0.005,          0,          0},
        {0,          0,      0.05,          0},
        {0,          0,         0,      0.005}*/
    };
    /*
    {0.0025,     0,      0,          0},
        {0, 0.00000025,      0,          0},
        {0,          0, 0.0025,          0},
        {0,          0,      0, 0.00000025}
     */
    public final double X0[] = new double[]{
        0, 0, 0, 0
    };
    public final double Xgoal[] = new double[]{
        0, 0, -10, 0
    };
    
    public final double M = 1000000;
    public double VMAX = 3;
    
    public double DELTA = 0.05;
    public int T = 10;
    public double AA = 3.236049505;
    public double BB = -1.787009505;
    
    
    private File file;
    private Polygon poly[];
    
    
    public int J;
    public double Rjti[][][];
    public double sum_t[][][];
    public double aji[][][];
    public double bji[][];
    
    @Override
    public String name() {
        return "Instance-TSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public double chi(int j, int t, int i){
        return -bji[j][i] - (AA+BB) * Rjti[j][t][i]; 
    }
    public double psi(int j, int t, int i){
        return -2*AA*(Rjti[j][t][i]+1e-9);
    }
    
    
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file = win.File("Instances for PPDCP",null,"sgl");
        DELTA = win.Dbl("Delta", 0.001, 0.0, 0.5);
        T = win.Int("K", 20, 1, 10000);
        P2 = win.Dbl("PD", 100, 0, 1e20);
        P1 = win.Dbl("PB", 10,  0, 1e20);
        
        P2 = 1e9;
        P1 = 100;
        T = 12;
        DELTA = 0.001;
    }
    private int width, heigth, cx, cy;
    private double zoon;
    private static final int MAX = 2;
    public static double P2 = 1e9;
    //public static final double P2 = 25;
    public static double P1 = 100;
    public double DELTA(double d) throws Exception{
        return d*P2;
    }
    public double BETA(double d){
        return d*P1;
    }
    @Override
    public void load() throws FileNotFoundException, IOException {
        
        Scanner in = new Scanner(new File("./config_dynamics_cartesian.sgl"));
        
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                A[i][j] = Double.parseDouble(in.next());
                System.out.println(A[i][j]);
            }
            in.nextLine();
        }
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            for(int j=0; j<2; j++){
                B[i][j] = Double.parseDouble(in.next());
            }
            in.nextLine();
        }
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                Q[i][j] = Double.parseDouble(in.next());
            }
            in.nextLine();
        }
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                P0[i][j] = Double.parseDouble(in.next());
            }
            in.nextLine();
        }
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
        DELTA = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        T = Integer.parseInt(in.nextLine());
        
        System.out.println(in.nextLine());
        P1 = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        P2 = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        width = Integer.parseInt(in.next());
        heigth = Integer.parseInt(in.next());
        in.nextLine();
        
        System.out.println(in.nextLine());
        cx = Integer.parseInt(in.next());
        cy = Integer.parseInt(in.next());
        in.nextLine();
        
        System.out.println(in.nextLine());
        zoon = Double.parseDouble(in.nextLine());
        
        
        in.close();
        
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
        
        
        Scanner sc = new Scanner(file);
        sc.nextLine();
        J = Integer.parseInt(sc.nextLine());
        //J = 0;
        poly = new Polygon[J];
        for(int j=0; j<J; j++){
            sc.nextLine();
            int x[] = uIO.toVectorInt(sc.nextLine());
            int y[] = uIO.toVectorInt(sc.nextLine());
            poly[j] = new Polygon(x, y, x.length);
        }
        sc.close();
        
        sum_t = new double[T][4][4];
        /*
        for(int t=0; t<T; t++){
            double R[][] = pow(A,t+1);
            System.out.printf("-------------- A[%d] ------------\n", t+1);
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    double v = R[i][j];
                    if(Math.abs(v)<0.0001){
                        System.out.printf(Locale.ENGLISH, "%8s ", ".");
                    }else{
                        System.out.printf(Locale.ENGLISH, "%8.4f ", v);
                    }
                }
                System.out.println();
            }
            R = pow(trans(A), t+1);
            System.out.printf("-------------- A'[%d] ------------\n", t+1);
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    double v = R[i][j];
                    if(Math.abs(v)<0.0001){
                        System.out.printf(Locale.ENGLISH, "%8s ", ".");
                    }else{
                        System.out.printf(Locale.ENGLISH, "%8.4f ", v);
                    }
                }
                System.out.println();
            }
            R = prod(pow(A,t+1), Q, pow(trans(A), t+1));
            System.out.printf("-------------- AxQxA'[%d] ------------\n", t+1);
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    double v = R[i][j];
                    if(Math.abs(v)<0.0001){
                        System.out.printf(Locale.ENGLISH, "%8s ", ".");
                    }else{
                        System.out.printf(Locale.ENGLISH, "%8.4f ", v);
                    }
                }
                System.out.println();
            }
        }
        */
        
        
        
        for(int t=0; t<T; t++){
            //sum_t[t] = prod(Q, 1);
            //sum_t[t] = prod(pow(A,+1), P0, pow(trans(A), +1));
            sum_t[t] = prod(pow(A,t+1), P0, pow(trans(A), t+1));
            //sum_t[t] = prod(pow(A,t+1), P0);
            //for(int i=0; i<Math.min(t, 0); i++){
            for(int i=0; i<t; i++){
                sum_t[t] = sum(sum_t[t], prod(pow(A,i), Q, pow(trans(A),i)));
                //sum_t[t] = sum(sum_t[t], prod(pow(A,i), Q));
            }
            //sum_t[t] = prod(sum_t[t], 1.0/200);
            
            /*sum_t[t] = prod(sum_t[t], 0.5);
            
            sum_t[t] = sum(sum_t[t], prod(prod(pow(A,t+1), P0, pow(trans(A), t+1)), 0.5));
            */
            //sum_t[t] = prod(pow(A,+1), P0, pow(trans(A), +1));
        }
        /*final double r0 = 0.024;
        final double rf = 0.072;
        //double max = 0;//Integer.MIN_VALUE;
        //double min = 0;//Integer.MIN_VALUE;
        double[] max = new double[T];
        //double min = Integer.MIN_VALUE;
        
        for(int t=0; t<T; t++){
            max[t] = Integer.MIN_VALUE;; 
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    //max += sum_t[T-1][i][j];
                    //min += sum_t[1][i][j];
                    max[t] = Math.max(max[t], sum_t[t][i][j]);
                    //min = Math.max(min, sum_t[1][i][j]);
                }
            }
         }
        for(int t=0; t<T; t++){
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    //sum_t[t][i][j] = sum_t[t][i][j]*(r0 + t*(rf-r0)/(T-1))/(min + t*(max-min)/(T-1));
                    sum_t[t][i][j] = sum_t[t][i][j]*(r0 + t*(rf-r0)/(T-1))/(max[t]);
                    if(t>0){
                        sum_t[t][i][j] =  Math.max(sum_t[t][i][j], sum_t[t-1][i][j]);
                    }
                }
            }
        }*/
        
        if(PRINT){
            for(int t=0; t<T; t++){
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
        
        aji = new double[J][][];
        bji = new double[J][];
        for(int j=0; j<J; j++){
            aji[j] = new double[poly[j].npoints][4];
            bji[j] = new double[poly[j].npoints];
            // a1 * x  + a3 * y  = b
            // ...
            // a1 * x1 + a3 * y1 = b
            // a1 * x2 + a3 * y2 = b
            
            // a3 = (b - a1 * x1)/y1
            // a1 * x2 + (b - a1 * x1)*y2/y1 = b
            // a1 = ( b - b*y2/y1 ) / (x2 - x1*y2/y1)
            // a1 = b*( y1 - y2 ) / (x2*y1 - x1*y2)
            // a3 = b*( x2 - x1 ) / (x2*y1 - x1*y2)
            
            for(int i=0; i<poly[j].npoints; i++){
                int k = (i+1) % poly[j].npoints;
                x1 = poly[j].xpoints[i]/100.0;
                y1 = poly[j].ypoints[i]/100.0;
                x2 = poly[j].xpoints[k]/100.0;
                y2 = poly[j].ypoints[k]/100.0;
                
                /*double ang = Math.atan2((y2-y1), x2-x1)*180/Math.PI;
                if(-90<ang && ang<+90){
                    if(Math.abs(y2-y1) > Math.abs(x1-x2)){
                        aji[j][i][0] = 1;
                        aji[j][i][2] = ( x1 - x2 ) / (y2 - y1);
                        bji[j][i] = aji[j][i][2] * y1  + x1;
                    }else{
                        aji[j][i][2] = 1;
                        aji[j][i][0] = ( y2 - y1 ) / (x1 - x2);
                        bji[j][i] = aji[j][i][0] * x1  + y1;
                    }
                    
                }else{
                    if(Math.abs(y2-y1) > Math.abs(x1-x2)){
                        aji[j][i][0] = 1;
                        aji[j][i][2] = ( x1 - x2 ) / (y2 - y1);
                        bji[j][i] = aji[j][i][2] * y1  + x1;
                    }else{
                        aji[j][i][2] = 1;
                        aji[j][i][0] = ( y2 - y1 ) / (x1 - x2);
                        bji[j][i] = aji[j][i][0] * x1  + y1;
                    }
                    //aji[j][i][0] = -aji[j][i][0];
                    //aji[j][i][2] = -aji[j][i][2];
                }
                System.out.printf("ang = %8d\n",(int)ang);
                */
                
                /*if(Math.abs(y2-y1) > Math.abs(x1-x2)){
                    aji[j][i][0] = 1;
                    aji[j][i][2] = ( x1 - x2 ) / (y2 - y1);
                    bji[j][i] = aji[j][i][2] * y1  + x1;
                }else{
                    aji[j][i][2] = 1;
                    aji[j][i][0] = ( y2 - y1 ) / (x1 - x2);
                    bji[j][i] = aji[j][i][0] * x1  + y1;
                }*/
                
                //x'
                aji[j][i][0] = -( y2 - y1 );
                //y'
                aji[j][i][2] = +( x2 - x1 );
                bji[j][i] = aji[j][i][0] * x1  + aji[j][i][2] * y1;
                
                
                
                /*bji[j][i] = 1;
                aji[j][i][0] = bji[j][i]*( y1 - y2 ) / (x2*y1 - x1*y2);
                aji[j][i][2] = bji[j][i]*( x2 - x1 ) / (x2*y1 - x1*y2);
                */
                if(PRINT)System.out.printf(Locale.ENGLISH, "%8.4f * x + %8.4f * y  = %8.4f\n", aji[j][i][0], aji[j][i][2], bji[j][i]);
            }
        }
        if(PRINT){
            for(int j=0; j<poly.length; j++){
                System.out.println("---------- poly["+j+"]------------");
                for(int i=0; i<poly[j].npoints; i++){
                    int k = (i+1) % poly[j].npoints;
                    System.out.printf("(%4d,%4d) -> (%4d,%4d)\n", poly[j].xpoints[i], poly[j].ypoints[i], poly[j].xpoints[k], poly[j].ypoints[k]);
                }
            }
        }
        
        
        Rjti = new double[J][T][];
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- Rj["+j+"]------------");
            for(int t=0; t<T; t++){
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
        plot("Start",null);
    }
    
    public static void main(String args[]){
        final double A[][] = new double[][]{
            {1, 1,  0,  0},
            {0, 1,  0,  0},
            {0, 0,  1,  1},
            {0, 0,  0,  1}
        };
        for(int t=0; t<10; t++){
            double R[][] = pow(A, t);
            System.out.println("-------------------["+t+"]-----------------");
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    System.out.printf("%3g ", R[i][j]);
                }
                System.out.println();
            }
        }
    }
    
    public static double[][] pow(double A[][], int n){
        if(n<=0){
            double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                R[i][i] = 1;
            }
            /*double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                for(int j=0; j<R[i].length; j++){
                    R[i][j] = A[i][j];
                }
            }*/
            /*double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                for(int j=0; j<R[i].length; j++){
                    R[i][j] = 0;
                }
            }*/
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
    private ArrayList<int[][][]> listZ = new ArrayList<int[][][]>();
    private double discretizacao = -1;
    public static final boolean PRINT = false;
    public static final boolean PLOT = true;
    public void clear(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listMU.clear();
            }
        }); 
    }
    private int tt = -1;
    public void plot(final double[][] vMt) {
        plot("title", vMt, null, -1);
    }
    public void plot(final double[][] vMt, double discretizacao) {
        this.discretizacao = discretizacao;
        plot("title", vMt, null, -1);
    }
    public void plot(String title, final double[][] vMt) {
        plot(title, vMt, null, -1);
    }
    public void plot(final String title, final double[][] vMt, final int[][][] Zjti, final int t) {
        
        if(vMt!=null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listMU.clear();
                    listMU.add(vMt);
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
                        pintar(g);
                    }
                };
                draw.setSize(width, heigth);
                frame.add(draw);
                frame.setSize(width+20, heigth+50);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
            draw.repaint();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setTitle(title);
            }
        }); 
    }
    public void save(){
        save("");
    }
    public void save(String name){
        BufferedImage img = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        pintar(g);
        try {
            ImageIO.write(img,   "png", new File("./sol_"+name+".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    public void pintar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width-1, heigth-1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width-1, heigth-1);
        g2.translate(cx, cy);
        g2.scale(zoon, zoon);
        
        
        
        
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;
        
        for(int k=0; k<=14; k++){
            g2.drawString(String.format("%d", (k-8)), (int)((k-8)*100)-10, (int)(0*100)+45);
            g2.drawString(String.format("%d", k), (int)(-8*100)-60, (int)(-k*100)+5);
            
            minX = Math.min(minX, ((k-8)*100)-10);
            maxX = Math.max(maxX, ((k-8)*100)-10);
            minY = Math.min(minY, (-k*100)+5);
            maxY = Math.max(maxY, (-k*100)+5);
        }
        
        /*if(discretizacao>0){
            
        }*/
        
        
        //g2.drawRect(50, 50, 400, 300);
        for(int j=0; j<J; j++){
            if(PRINT)System.out.println("---------- poly["+j+"]------------");
            for(int i=0; i<poly[j].npoints; i++){
                int k = (i+1) % poly[j].npoints;
                if(PRINT)System.out.printf("(%4d,%4d) -> (%4d,%4d)\n", poly[j].xpoints[i], poly[j].ypoints[i], poly[j].xpoints[k], poly[j].ypoints[k]);
            }
            if(PRINT)System.out.println("---------- lines["+j+"]------------");
            for(int i=0; i<poly[j].npoints; i++){
                double x1, y1;
                if(Math.abs(aji[j][i][2])>0.01){
                    x1 = poly[j].xpoints[i]/100.0;
                    y1 = ( bji[j][i] - aji[j][i][0] * x1 ) / aji[j][i][2];
                }else{
                    y1 = poly[j].ypoints[i]/100.0;
                    x1 = ( bji[j][i] - aji[j][i][2] * y1 ) / aji[j][i][0];
                }


                int k = (i+1) % poly[j].npoints;

                double x2, y2;
                if(Math.abs(aji[j][i][2])>0.01){
                    x2 = poly[j].xpoints[k]/100.0;
                    y2 = ( bji[j][k] - aji[j][k][0] * x2 ) / aji[j][k][2];
                }else{
                    y2 = poly[j].ypoints[k]/100.0;
                    x2 = ( bji[j][k] - aji[j][k][2] * y2 ) / aji[j][k][0];
                }


                double S = 30/Math.sqrt(aji[j][i][0]*aji[j][i][0] + aji[j][i][2]*aji[j][i][2]);
                if(PRINT)System.out.printf("(%4d,%4d) -> (%4d,%4d)\n", (int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                //g2.drawPolygon(poly[j]);
                g2.drawLine((int)((x1+x2)*50), (int)((y1+y2)*50), (int)((x1+x2)*50+S*aji[j][i][0]), (int)((y1+y2)*50+S*aji[j][i][2]));
                g2.fillOval((int)((x1+x2)*50-5), (int)((y1+y2)*50-5), 10, 10);
                g2.fillOval((int)((x1+x2)*50+S*aji[j][i][0]-5), (int)((y1+y2)*50+S*aji[j][i][2]-5), 10, 10);

            }
            double xm = 0;
            double ym = 0;
            for(int i=0; i<poly[j].npoints; i++){
                xm += poly[j].xpoints[i]/100.0;
                ym += poly[j].ypoints[i]/100.0;
            }
            xm = xm / poly[j].npoints;
            ym = ym / poly[j].npoints;
            g2.drawString(String.format("[%d]", j), (int)(xm*100)-20, (int)(ym*100)+5);
        }
        
        g2.setStroke(new BasicStroke(3)); 
        for(int Zjti[][][] : listZ){
            int t = tt;
            g2.setColor(Color.BLUE);
            for(int j=0; j<J; j++){
                for(int i : Zjti[j][t]){
                    double x1, y1;
                    if(Math.abs(aji[j][i][2])>0.01){
                        x1 = poly[j].xpoints[i]/100.0;
                        y1 = ( bji[j][i] - aji[j][i][0] * x1 ) / aji[j][i][2];
                    }else{
                        y1 = poly[j].ypoints[i]/100.0;
                        x1 = ( bji[j][i] - aji[j][i][2] * y1 ) / aji[j][i][0];
                    }


                    int k = (i+1) % poly[j].npoints;

                    double x2, y2;
                    if(Math.abs(aji[j][i][2])>0.01){
                        x2 = poly[j].xpoints[k]/100.0;
                        y2 = ( bji[j][k] - aji[j][k][0] * x2 ) / aji[j][k][2];
                    }else{
                        y2 = poly[j].ypoints[k]/100.0;
                        x2 = ( bji[j][k] - aji[j][k][2] * y2 ) / aji[j][k][0];
                    }
                    g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                }
            }
        }
        g2.setStroke(new BasicStroke(1));    
        g2.setColor(Color.BLACK);
        g2.fillRect((int)(-10+Xgoal[0]*100), (int)(-10+Xgoal[2]*100), 20, 20);
        g2.fillOval((int)(-10+X0[0]*100), (int)(-10+X0[2]*100), 20, 20);
        g2.drawRect(-800, -1100, 1400, 1100);

        for(double [][] vMUt : listMU){
            g2.setColor(Color.red);
            if(tt==-1){
                for(int t=0; t<T-1; t++){
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];
                    double x2 = vMUt[t+1][0];
                    double y2 = vMUt[t+1][2];
                    g2.drawLine((int)(x1*100), (int)(y1*100), (int)(x2*100), (int)(y2*100));
                    g2.fillOval((int)(x1*100-5), (int)(y1*100-5), 10, 10);
                    g2.fillOval((int)(x2*100-5), (int)(y2*100-5), 10, 10);


                }
                for(int t=0; t<T; t++){
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

                    g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
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

                g2.drawOval((int)(x1*100-cx*100), (int)(y1*100-cy*100), (int)(2*cx*100), (int)(2*cy*100));
            }
                
        }
    }
    
    

    public static double norm2(double[] A, double[] B) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += (A[i]-B[i])*(A[i]-B[i]);
        }
        return Math.sqrt(r);
    }
    public static double norm1(double[] A, double[] B) {
        double r = 0;
        for(int i=0; i<A.length; i++){
            r += Math.abs(A[i]-B[i]);
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
    
    private static final double table[] = new double[MAX];
    private static final boolean flag[] = new boolean[MAX];
    
    private static double Ck(int k){
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
        return (uUtil.dist(X0[0], Xgoal[0], X0[2], Xgoal[2])+0.99999);
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
    
}
