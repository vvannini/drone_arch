/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF;

import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author marcio
 */
public class TestesAlfa {
    
    
    private static final int TEST = 3;  //[1 .. 2]
    private static final int REST = 6;  //[1 ... 6]
    
    //Obstacle
    private static final int N = 6;
    private static final int B = (int)(100*Math.cos(2*Math.PI/(2*N)));//(int)(100/Math.sin(90-360.0/(N*2.0)));
    
    //Only for TEST = 1
    private static final int M = 20;
    
    //Only for TEST = 2
    private static final int T = 20;
    
    //Only for TEST = 3
    private static final double density = (2.0*N)/(N-2.0)+1;//12/N;
    private static final double distance = 2.5;
    
    
    
    public static void addRestrictions1(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
        //Segunda restrição
        for(int t=0; t<Zi.length; t++){
            IloNumExpr exp = null;
            for (int i = 0; i < N; i++) {
                exp = cplex.SumProd(exp, 1, Zi[t][i]);
            }
            cplex.addGe(exp, 1, "Sum");
        }
    }
    public static void addRestrictions2(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
        
        IloNumVar alfa[][][] = cplex.numVarArray(Zi.length, N, N, 0, 1, "alfa");
        
        for (int i = 0; i < N; i++) {
             for (int l = 0; l < N; l++) {
                if (l != i) {
                    IloNumExpr exp0 = cplex.sum(cplex.prod(alfa[0][i][l], -1), Zi[0][i], Zi[0][l]);
                    cplex.addLe(exp0, 1, "NEWexp0." + (i + 1) + "," + (l + 1));
                    for(int t=1; t<Zi.length; t++){
                        IloNumExpr exp1 = cplex.sum(cplex.prod(alfa[t][i][l], -1), Zi[t][i], cplex.prod(Zi[t-1][i], -1), Zi[t][l], cplex.prod(Zi[t-1][l], -1));
                        cplex.addLe(exp1, 1, "NEWexp1." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp2 = cplex.sum(cplex.prod(alfa[t][i][l], -1), Zi[t][i], cplex.prod(Zi[t-1][i], -1), cplex.prod(Zi[t][l], -1), Zi[t-1][l]);
                        cplex.addLe(exp2, 1, "NEWexp2." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp3 = cplex.sum(cplex.prod(alfa[t][i][l], -1), cplex.prod(Zi[t][i], -1), Zi[t-1][i], Zi[t][l], cplex.prod(Zi[t-1][l], -1));
                        cplex.addLe(exp3, 1, "NEWexp3." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp4 = cplex.sum(cplex.prod(alfa[t][i][l], -1), cplex.prod(Zi[t][i], -1), Zi[t-1][i], cplex.prod(Zi[t][l], -1), Zi[t-1][l]);
                        cplex.addLe(exp4, 1, "NEWexp4." + (i + 1) + "," + (l + 1));
                    }
                } else {
                    for(int t=0; t<Zi.length; t++){
                        alfa[t][i][l].setUB(0);
                    }
                }
            }
        }
        for(int t=0; t<Zi.length; t++){
            for (int i = 0; i < N; i++) {
                 for (int l = 0; l < N; l++) {
                     if (l != i) {
                         cplex.addEq(alfa[t][i][l], alfa[t][l][i], "Eq."+ (i + 1) + "," + (l + 1));
                     }
                 }
            }
        }
        
        for(int t=0; t<Zi.length; t++){
            IloNumExpr exp = null;
            for (int i = 0; i < N; i++) {
                exp = cplex.SumProd(exp, 1, Zi[t][i]);
            }
            for (int i = 0; i < N; i++) {
                for (int l = 0; l < N; l++) {
                    exp = cplex.SumProd(exp, -1, alfa[t][i][l]);
                }
            }
            cplex.addGe(exp, 1, "Sum1");
        }
    }
    public static void addRestrictions3(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
        
        for (int i = 0; i < N; i++) {
             for (int l = 0; l < N; l++) {
                if (l != i) {
                    IloNumExpr exp0 = cplex.sum(Zi[0][i], Zi[0][l]);
                    cplex.addLe(exp0, 1, "NEWexp1." + (i + 1) + "," + (l + 1));
                    for(int t=1; t<Zi.length; t++){
                        IloNumExpr exp1 = cplex.sum(Zi[t][i], cplex.prod(Zi[t-1][i], -1), Zi[t][l], cplex.prod(Zi[t-1][l], -1));
                        cplex.addLe(exp1, 1, "NEWexp1." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp2 = cplex.sum(Zi[t][i], cplex.prod(Zi[t-1][i], -1), cplex.prod(Zi[t][l], -1), Zi[t-1][l]);
                        cplex.addLe(exp2, 1, "NEWexp2." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp3 = cplex.sum(cplex.prod(Zi[t][i], -1), Zi[t-1][i], Zi[t][l], cplex.prod(Zi[t-1][l], -1));
                        cplex.addLe(exp3, 1, "NEWexp3." + (i + 1) + "," + (l + 1));

                        IloNumExpr exp4 = cplex.sum(cplex.prod(Zi[t][i], -1), Zi[t-1][i], cplex.prod(Zi[t][l], -1), Zi[t-1][l]);
                        cplex.addLe(exp4, 1, "NEWexp4." + (i + 1) + "," + (l + 1));
                    }
                }
            }
        }
        for(int t=0; t<Zi.length; t++){
            IloNumExpr exp = null;
            for (int i = 0; i < N; i++) {
                exp = cplex.SumProd(exp, 1, Zi[t][i]);
            }
            cplex.addGe(exp, 1, "Sum1");
        }
    }
    public static void addRestrictions4(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
        //Segunda restrição
//        for(int t=0; t<Zi.length; t++){
//            IloNumExpr exp = null;
//            for (int i = 0; i < N; i++) {
//                exp = cplex.SumProd(exp, 1, Zi[t][i]);
//            }
//            cplex.addGe(exp, 1, "Sum1");
//        }
        IloNumExpr exp = null;
        for (int i = 0; i < N; i++) {
            exp = cplex.SumProd(exp, 1, Zi[Zi.length-1][i]);
        }
        cplex.addGe(exp, 1, "Sum1");
        
        for(int t=1; t<Zi.length; t++){
            for (int i = 0; i < N; i++) {
                LinkedList<IloNumVar> Pi = new LinkedList<IloNumVar>();
                for (int l = 0; l < N; l++) {
                    if(i!=l){
                        Pi.addLast(cplex.And(Zi[t][l], Zi[t-1][l]));
                    }
                }
                Pi.addLast(Zi[t-1][i]);

                cplex.addLe(Zi[t][i], cplex.Or("and", Pi.toArray(new IloNumVar[Pi.size()]))); 
            }
        }
    }
    public static void addRestrictions5(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
//        for(int t=0; t<Zi.length; t++){
//            IloNumExpr exp = null;
//            for (int i = 0; i < N; i++) {
//                exp = cplex.SumProd(exp, 1, Zi[t][i]);
//            }
//            cplex.addGe(exp, 1, "Sum1");
//        }
        IloNumExpr exp = null;
        for (int i = 0; i < N; i++) {
            exp = cplex.SumProd(exp, 1, Zi[Zi.length-1][i]);
        }
        cplex.addGe(exp, 1, "Sum1");
        for(int t=1; t<Zi.length; t++){
            for (int i = 0; i < N; i++) {
                LinkedList<IloNumVar> Pi = new LinkedList<IloNumVar>();

                int l = (i+1)%N;
                Pi.addLast(cplex.And(Zi[t][l], Zi[t-1][l]));

                l = (i-1+N)%N;
                Pi.addLast(cplex.And(Zi[t][l], Zi[t-1][l]));

                Pi.addLast(Zi[t-1][i]);

                cplex.addLe(Zi[t][i], cplex.Or("and", Pi.toArray(new IloNumVar[Pi.size()]))); 
            }
        }
    }
    public static void addRestrictions6(CplexExtended cplex, IloIntVar Zi[][]) throws IloException{
        //Segunda restrição
//        IloNumExpr exp = null;
//        for (int i = 0; i < N; i++) {
//            exp = cplex.SumProd(exp, 1, Zi[0][i]);
//        }
//        cplex.addGe(exp, 1, "Sum1");
        
        for(int t=1; t<Zi.length; t++){
            LinkedList<IloNumVar> Pi = new LinkedList<IloNumVar>();
            for (int i = 0; i < N; i++) {
                Pi.addLast(cplex.And(Zi[t][i], Zi[t-1][i]));
            }
            cplex.addEq(cplex.sum(Pi.toArray(new IloNumVar[Pi.size()])), 1); 
        }
    }
    
    public static void main(String[] args) throws Exception {
        if(TEST==1){
            main1(args);
        }else if(TEST==2){
            main2(args);
        }else if(TEST==3){
            main3(args);
        }
    }
    
    public static void main1(String[] args) throws Exception {
        double time = System.nanoTime();
        
        CplexExtended cplex = new CplexExtended();
        
        
        
        IloNumVar Xt[][] = cplex.numVarArray(2, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, "Xt");
        IloIntVar Zi[][] = cplex.boolVarArray(2, N, "Zi");
        
        switch(REST){
            case 1: addRestrictions1(cplex, Zi);    break;
            case 2: addRestrictions2(cplex, Zi);    break;
            case 3: addRestrictions3(cplex, Zi);    break;
            case 4: addRestrictions4(cplex, Zi);    break;
            case 5: addRestrictions5(cplex, Zi);    break;
            case 6: addRestrictions6(cplex, Zi);    break;
        }
        
        
        //-------------------------------- (36) --------------------------------
        final double bigM = 100000;
        final double R = 0;
        for(int t=0; t<2; t++){
            for(int i=0; i<N; i++){
                IloNumExpr aux = cplex.sum(cplex.prod(-ax(i), Xt[t][0]),cplex.prod(-ay(i), Xt[t][1]));
                aux = cplex.sum(aux, cplex.prod(bigM, Zi[t][i]));

                double val = bigM - b(i) - R;
                cplex.addLe(aux, val, "S36."+(t+1)+","+(i+1)+"");
            }
        }
        
        cplex.setOut(null);
        cplex.setWarning(null);
        System.out.printf("[%5d %5d %5d]\n", cplex.getNbinVars(), cplex.getNcols()-cplex.getNbinVars(), cplex.getNrows());
        
        final LinkedList<Integer[]> list = new LinkedList<Integer[]>();
        
        int cont_yes = 0;
        int cont_no = 0;
        
        for(int m=0; m<M; m++){
            int x0[] = {(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            while(pertence(x0[0], x0[1], R)){
                x0 = new int[]{(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            }
            //int x1[] = x0;
            int x1[] = {(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            while(pertence(x1[0], x1[1], R)){
                x1 = new int[]{(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            }
            for(int n=0; n<2; n++){
                Xt[0][n].setLB(x0[n]);
                Xt[0][n].setUB(x0[n]);

                Xt[1][n].setLB(x1[n]);
                Xt[1][n].setUB(x1[n]);
            }
            if(cplex.solve()){
                list.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],1});
                cont_yes++;
            }else{
                list.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],0});
                cont_no++;
            }
            if(m==M-1){
                System.out.printf("%5.2f : %3d  %3d\n", m*100.0/M, cont_yes, cont_no);
            }
            //System.out.printf("%5.2f : %3d  %3d\n", m*100.0/M, cont_yes, cont_no);
        }
        
        time = System.nanoTime()-time;
        System.out.printf("Time = %g\n", time/1e9);
        
        JFrame frame = new JFrame();
        frame.setSize(620, 670);
        final JPanel draw = new JPanel(){
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 600, 600);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 600, 600);
                
                
                if(MODE==4){
                    g.setColor(Color.LIGHT_GRAY);  //All
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("All situations", 20, 617);
                }
                if(MODE==0){
                    g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, avoidance collision correctly detected by model", 20, 617);
                }
                if(MODE==1){
                    g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, collision with obstacle correctly detected by model", 20, 617);
                }
                if(MODE==2){
                    g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Warning, do not have collision with the obstacle but the model disagree", 20, 617);
                }
                if(MODE==3){
                    g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Danger, collision occur but the model did not detect.", 20, 617);
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, 600, 600, 40);
                
                g.setColor(Color.LIGHT_GRAY);
                for(int i=0; i<600; i++){
                    for(int j=0; j<600; j++){
                        double y = i - 300;
                        double x = j - 300;
                        if(pertence(x,y,0)){
                            g.drawRect(j, i, 1, 1);
                        }
                    }
                }
                
                
                
                Polygon poly = new Polygon();
                for(int i=0; i<N; i++){
                    double r = b(i)*1.05/Math.cos(2*Math.PI/(2*N));
                    poly.addPoint((int)(Math.cos(2*i*Math.PI/(N))*r+300), (int)(Math.sin(2*i*Math.PI/(N))*r+300));
                }
                g.setColor(new Color(64, 64, 64, 128));
                g.fillPolygon(poly);
                g.setColor(Color.BLACK);
                g.drawPolygon(poly);
                
                
                
                g.setColor(Color.BLUE);
                for(int i=0; i<N; i++){
                    int x0 = (int)(ax(i)*b(i)+300);
                    int y0 = (int)(ay(i)*b(i)+300);
                    int x1 = (int)(ax(i)*b(i)*1.2+300);
                    int y1 = (int)(ay(i)*b(i)*1.2+300);
                    
                    g.drawLine(x0, y0, x1, y1);
                    g.fillOval(x0-5, y0-5, 10, 10);
                    g.fillRect(x1-5, y1-5, 10, 10);
                }
                
                for(Integer[] p: list){
                    
                    if(p[4]>0.5){
                        if(intercection(p, poly)){
                            if(MODE==3 || MODE==4){
                                g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }else{
                            if(MODE==0 || MODE==4){
                                g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }
                    }else{
                        if(intercection(p, poly)){
                            if(MODE==1 || MODE==4){
                                g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }else{
                            if(MODE==2 || MODE==4){
                                g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }
                    }
                }
            }
        };
        draw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                MODE = (MODE+1)%5;
                draw.repaint();
            }
        });
        draw.setBackground(Color.BLACK);
        frame.add(draw);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    public static void main2(String[] args) throws Exception {
        double time = System.nanoTime();
        
        CplexExtended cplex = new CplexExtended();
        
        IloNumVar Xt[][] = cplex.numVarArray(T+1, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, "Xt");
        IloNumVar Ut[][] = cplex.numVarArray(T, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, "Ut");
        IloIntVar Zi[][] = cplex.boolVarArray(T+1, N, "Zi");
        
        switch(REST){
            case 1: addRestrictions1(cplex, Zi);    break;
            case 2: addRestrictions2(cplex, Zi);    break;
            case 3: addRestrictions3(cplex, Zi);    break;
            case 4: addRestrictions4(cplex, Zi);    break;
            case 5: addRestrictions5(cplex, Zi);    break;
            case 6: addRestrictions6(cplex, Zi);    break;
        }
        
        
        //-------------------------------- (36) --------------------------------
        final double bigM = 100000;
        final double R = 0;
        for(int t=0; t<T+1; t++){
            for(int i=0; i<N; i++){
                IloNumExpr aux = cplex.sum(cplex.prod(-ax(i), Xt[t][0]),cplex.prod(-ay(i), Xt[t][1]));
                aux = cplex.sum(aux, cplex.prod(bigM, Zi[t][i]));

                double val = bigM - b(i) - R;
                cplex.addLe(aux, val, "S36."+(t+1)+","+(i+1)+"");
            }
        }
        
        for(int t=0; t<T; t++){
            for(int n=0; n<2; n++){
                cplex.addEq(Xt[t+1][n], cplex.sum(Xt[t][n], Ut[t][n]));
            }
        }
        
        IloNumExpr obj = null;
        for(int t=0; t<T; t++){
            obj = cplex.SumNumScalProd(obj, "obj", 32, 50, Ut[t]);
        }
        cplex.addMinimize(obj);
        
        cplex.setOut(null);
        cplex.setWarning(null);
        System.out.printf("[%5d %5d %5d]\n", cplex.getNbinVars(), cplex.getNcols()-cplex.getNbinVars(), cplex.getNrows());
        
        
        final Polygon poly = new Polygon();
        for(int i=0; i<N; i++){
            double r = b(i)*0.98/Math.cos(2*Math.PI/(2*N));
            poly.addPoint((int)(Math.cos(2*i*Math.PI/(N))*r+300), (int)(Math.sin(2*i*Math.PI/(N))*r+300));
        }

        final LinkedList<Integer[]> list = new LinkedList<Integer[]>();
        final LinkedList<Integer[]> dest = new LinkedList<Integer[]>();
        
        int cont_yes = 0;
        int cont_no = 0;
        
        int cont_0 = 0;
        int cont_1 = 0;
        int cont_2 = 0;
        int cont_3 = 0;
        
        for(int m=0; m<M; m++){
            int x0[] = {(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            while(pertence(x0[0], x0[1], R)){
                x0 = new int[]{(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            }
            //int x1[] = x0;
            int x1[] = {(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            while(pertence(x1[0], x1[1], R)){
                x1 = new int[]{(int)(Math.random()*600-300),(int)(Math.random()*600-300)};
            }
            for(int n=0; n<2; n++){
                Xt[0][n].setLB(x0[n]);
                Xt[0][n].setUB(x0[n]);

                Xt[T][n].setLB(x1[n]);
                Xt[T][n].setUB(x1[n]);
            }
//            double dist = Math.sqrt((x1[0]-x0[0])*(x1[0]-x0[0]) + (x1[1]-x0[1])*(x1[1]-x0[1]));
//            for(int t=0; t<T; t++){
//                for(int n=0; n<2; n++){
//                    Ut[0][n].setLB(-dist*factor/T);
//                    Ut[0][n].setUB(+dist*factor/T);
//                }
//            }
            
            if(cplex.solve()){
                int Class = 0;
                for(int t=0; t<T; t++){
                    Integer p[] = new Integer[]{
                        (int)cplex.getValue(Xt[t][0]),
                        (int)cplex.getValue(Xt[t][1]),
                        (int)cplex.getValue(Xt[t+1][0]),
                        (int)cplex.getValue(Xt[t+1][1])};
                    if(intercection(p, poly)){
                        Class = 3;
                        break;
                    }
                }
                if(Class==3){
                    cont_3++;
                }else{
                    cont_0++;
                }
                for(int t=0; t<T; t++){
                    list.add(new Integer[]{
                        (int)cplex.getValue(Xt[t][0]),
                        (int)cplex.getValue(Xt[t][1]),
                        (int)cplex.getValue(Xt[t+1][0]),
                        (int)cplex.getValue(Xt[t+1][1]),Class});
                }
                cont_yes++;
                dest.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],Class});
            }else{
                int Class = 2;
//                
//                Integer p[] = new Integer[]{x0[0],x0[1],x1[0],x1[1]};
//                if(intercection(p, poly)){
//                    Class = 1;
//                }
//                
//                if(Class==1){
//                    cont_1++;
//                }else{
//                    cont_2++;
//                }
                list.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],Class});
                cont_no++;
                dest.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],Class});
            }
            if(m%(M/10)==0){
                System.out.printf("%5.2f\n", m*100.0/M);
            }
            if(m==M-1){
                System.out.printf("%5.2f : [ %3d  %3d ] [ %3d  %3d %3d  %3d ]\n", m*100.0/M, cont_yes, cont_no, cont_0, cont_1, cont_2, cont_3);
            }
            //System.out.printf("%5.2f : %3d  %3d\n", m*100.0/M, cont_yes, cont_no);
        }
        
        time = System.nanoTime()-time;
        System.out.printf("Time = %g\n", time/1e9);
        
        JFrame frame = new JFrame();
        frame.setSize(620, 670);
        final JPanel draw = new JPanel(){
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 600, 600);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 600, 600);
                
                
                if(MODE==4){
                    g.setColor(Color.LIGHT_GRAY);  //All
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("All situations", 20, 617);
                }
                if(MODE==0){
                    g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, avoidance collision correctly detected by model", 20, 617);
                }
                if(MODE==1){
                    g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, collision with obstacle correctly detected by model", 20, 617);
                }
                if(MODE==2){
                    g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Warning, do not have collision with the obstacle but the model disagree", 20, 617);
                }
                if(MODE==3){
                    g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Danger, collision occur but the model did not detect.", 20, 617);
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, 600, 600, 40);
                
                g.setColor(Color.LIGHT_GRAY);
                for(int i=0; i<600; i++){
                    for(int j=0; j<600; j++){
                        double y = i - 300;
                        double x = j - 300;
                        if(pertence(x,y,0)){
                            g.drawRect(j, i, 1, 1);
                        }
                    }
                }
                
                
                
                
                g.setColor(new Color(64, 64, 64, 128));
                g.fillPolygon(poly);
                g.setColor(Color.BLACK);
                g.drawPolygon(poly);
                
                
                
                g.setColor(Color.BLUE);
                for(int i=0; i<N; i++){
                    int x0 = (int)(ax(i)*b(i)+300);
                    int y0 = (int)(ay(i)*b(i)+300);
                    int x1 = (int)(ax(i)*b(i)*1.2+300);
                    int y1 = (int)(ay(i)*b(i)*1.2+300);
                    
                    g.drawLine(x0, y0, x1, y1);
                    g.fillOval(x0-5, y0-5, 10, 10);
                    g.fillRect(x1-5, y1-5, 10, 10);
                }
                
                for(Integer[] p: list){
                    if(p[4]==0){
                        if(MODE==0 || MODE==4){
                            g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                            g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                            g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                            g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                        }
                    }else if(p[4]==1){
                        if(MODE==1 || MODE==4){
                            g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                            g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                            g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                            g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                        }
                    }else if(p[4]==2){
                        if(MODE==2 || MODE==4){
                            g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                            g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                            g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                            g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                        }
                    }else if(p[4]==3){
                        if(MODE==3 || MODE==4){
                            g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                            g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                            g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                            g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                        }
                    }
//                    if(p[4]>0.5){
//                        if(intercection(p, poly)){
//                            if(MODE==3 || MODE==4){
//                                g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
//                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
//                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
//                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
//                            }
//                        }else{
//                            if(MODE==0 || MODE==4){
//                                g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
//                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
//                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
//                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
//                            }
//                        }
//                    }else{
//                        if(intercection(p, poly)){
//                            if(MODE==1 || MODE==4){
//                                g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
//                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
//                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
//                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
//                            }
//                        }else{
//                            if(MODE==2 || MODE==4){
//                                g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
//                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
//                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
//                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
//                            }
//                        }
//                    }
                }
                
                for(Integer[] p: dest){
                    final int r = 5;
                    
                    if(p[4]==0){
                        if(MODE==0 || MODE==4){
                            g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                            g.fillOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.fillRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);

                            g.setColor(Color.BLACK);
                            g.drawOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.drawRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);
                        }
                    }else if(p[4]==1){
                        if(MODE==1 || MODE==4){
                            g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                            g.fillOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.fillRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);

                            g.setColor(Color.BLACK);
                            g.drawOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.drawRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);
                        }
                    }else if(p[4]==2){
                        if(MODE==2 || MODE==4){
                            g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                            g.fillOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.fillRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);

                            g.setColor(Color.BLACK);
                            g.drawOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.drawRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);
                        }
                    }else if(p[4]==3){
                        if(MODE==3 || MODE==4){
                            g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                            g.fillOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.fillRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);

                            g.setColor(Color.BLACK);
                            g.drawOval(p[0]+300-r, p[1]+300-r, 2*r, 2*r);
                            g.drawRect(p[2]+300-r, p[3]+300-r, 2*r, 2*r);
                        }
                    }
                    
                   
                    
                }
            }
        };
        draw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                MODE = (MODE+1)%5;
                draw.repaint();
            }
        });
        draw.setBackground(Color.BLACK);
        frame.add(draw);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    public static void main3(String[] args) throws Exception {
        double time = System.nanoTime();
        
        CplexExtended cplex = new CplexExtended();

        IloNumVar Xt[][] = cplex.numVarArray(2, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, "Xt");
        IloIntVar Zi[][] = cplex.boolVarArray(2, N, "Zi");
        
        switch(REST){
            case 1: addRestrictions1(cplex, Zi);    break;
            case 2: addRestrictions2(cplex, Zi);    break;
            case 3: addRestrictions3(cplex, Zi);    break;
            case 4: addRestrictions4(cplex, Zi);    break;
            case 5: addRestrictions5(cplex, Zi);    break;
            case 6: addRestrictions6(cplex, Zi);    break;
        }
        
        
        //-------------------------------- (36) --------------------------------
        final double bigM = 100000;
        final double R = 0;
        for(int t=0; t<2; t++){
            for(int i=0; i<N; i++){
                IloNumExpr aux = cplex.sum(cplex.prod(-ax(i), Xt[t][0]),cplex.prod(-ay(i), Xt[t][1]));
                aux = cplex.sum(aux, cplex.prod(bigM, Zi[t][i]));

                double val = bigM - b(i) - R;
                cplex.addLe(aux, val, "S36."+(t+1)+","+(i+1)+"");
            }
        }
        
        cplex.setOut(null);
        cplex.setWarning(null);
        System.out.printf("[%5d %5d %5d]\n", cplex.getNbinVars(), cplex.getNcols()-cplex.getNbinVars(), cplex.getNrows());
        
        final LinkedList<Integer[]> list = new LinkedList<Integer[]>();
        
        int cont_yes = 0;
        int cont_no = 0;
        
        int m=0;
        for(int i=0; i<N*density; i++){
            for(int j=0; j<N*density; j++){
                if(i!=j){
                    
                    //B = (int)(100*Math.cos(2*Math.PI/(2*N)));
                    double r0 = b(j)*distance/Math.cos(2*Math.PI/(N*2));
                    int x0[] = {(int)(Math.cos(2*j*Math.PI/(N*density))*r0), (int)(Math.sin(2*j*Math.PI/(N*density))*r0)};
                    
                    double r1 = b(i)*distance/Math.cos(2*Math.PI/(N*2));
                    int x1[] = {(int)(Math.cos(2*i*Math.PI/(N*density))*r1), (int)(Math.sin(2*i*Math.PI/(N*density))*r1)};
                    for(int n=0; n<2; n++){
                        Xt[0][n].setLB(x0[n]);
                        Xt[0][n].setUB(x0[n]);

                        Xt[1][n].setLB(x1[n]);
                        Xt[1][n].setUB(x1[n]);
                    }
                    if(cplex.solve()){
                        list.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],1});
                        cont_yes++;
                    }else{
                        list.add(new Integer[]{x0[0],x0[1],x1[0],x1[1],0});
                        cont_no++;
                    }
                }
                m++;
            }
        }
        System.out.printf("%5.2f : %3d  %3d\n", m*100.0/(N*density*N*density), cont_yes, cont_no);
        
        time = System.nanoTime()-time;
        System.out.printf("Time = %g\n", time/1e9);
        
        JFrame frame = new JFrame();
        frame.setSize(620, 670);
        final JPanel draw = new JPanel(){
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 600, 600);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 600, 600);
                
                
                if(MODE==4){
                    g.setColor(Color.LIGHT_GRAY);  //All
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("All situations", 20, 617);
                }
                if(MODE==0){
                    g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, avoidance collision correctly detected by model", 20, 617);
                }
                if(MODE==1){
                    g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Ok, collision with obstacle correctly detected by model", 20, 617);
                }
                if(MODE==2){
                    g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Warning, do not have collision with the obstacle but the model disagree", 20, 617);
                }
                if(MODE==3){
                    g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                    g.fillRect(0, 600, 600, 40);
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD));
                    g.drawString("Danger, collision occur but the model did not detect.", 20, 617);
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, 600, 600, 40);
                
                g.setColor(Color.LIGHT_GRAY);
                for(int i=0; i<600; i++){
                    for(int j=0; j<600; j++){
                        double y = i - 300;
                        double x = j - 300;
                        if(pertence(x,y,0)){
                            g.drawRect(j, i, 1, 1);
                        }
                    }
                }
                
                
                
                Polygon poly = new Polygon();
                for(int i=0; i<N; i++){
                    double r = b(i)*1.05/Math.cos(2*Math.PI/(2*N));
                    poly.addPoint((int)(Math.cos(2*i*Math.PI/(N))*r+300), (int)(Math.sin(2*i*Math.PI/(N))*r+300));
                }
                g.setColor(new Color(64, 64, 64, 128));
                g.fillPolygon(poly);
                g.setColor(Color.BLACK);
                g.drawPolygon(poly);
                
                
                final boolean paintNormal = false;
                if(paintNormal){
                    g.setColor(Color.BLUE);
                    for(int i=0; i<N; i++){
                        int x0 = (int)(ax(i)*b(i)+300);
                        int y0 = (int)(ay(i)*b(i)+300);
                        int x1 = (int)(ax(i)*b(i)*1.2+300);
                        int y1 = (int)(ay(i)*b(i)*1.2+300);

                        g.drawLine(x0, y0, x1, y1);
                        g.fillOval(x0-5, y0-5, 10, 10);
                        g.fillRect(x1-5, y1-5, 10, 10);
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                for(Integer[] p: list){
                    
                    if(p[4]>0.5){
                        if(intercection(p, poly)){
                            if(MODE==3 || MODE==4){
                                g.setColor(Color.RED);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }else{
                            if(MODE==0 || MODE==4){
                                g.setColor(Color.GREEN);  //Bom situação correta detectada, não houve colisão com o obstáculo e a restrição confirmou isso
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }
                    }else{
                        if(intercection(p, poly)){
                            if(MODE==1 || MODE==4){
                                g.setColor(Color.YELLOW);   //Bom situação correta detectada, a restrição deu infactível e a rota também é infactível
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }else{
                            if(MODE==2 || MODE==4){
                                g.setColor(Color.MAGENTA);  //Warning, exesso de segurança, a restrição acusou infactibilidade mas a rota não é infactível.
                                g.drawLine(p[0]+300, p[1]+300, p[2]+300, p[3]+300);
                                g.fillOval(p[0]+300-5, p[1]+300-5, 10, 10);
                                g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                            }
                        }
                    }
                }
                for(Integer[] p: list){
                    g.setColor(Color.BLACK);  //Perigo colisão não dectectada, houve colisão com o obstáculo mas não foi calculado pela restrição
                    g.fillRect(p[2]+300-5, p[3]+300-5, 10, 10);
                }
            }
        };
        draw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                MODE = (MODE+1)%5;
                draw.repaint();
            }
        });
        draw.setBackground(Color.BLACK);
        frame.add(draw);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private static int MODE = 4;
    private static boolean intercection(Integer[] p, Polygon poly) {
        int x0 = p[0]+300;
        int y0 = p[1]+300;
        int x1 = p[2]+300;
        int y1 = p[3]+300;
        for(int m=0; m<100; m++){
            double f = m/100.0;
            double mx = x0*f + x1*(1-f);
            double my = y0*f + y1*(1-f);
            if(poly.contains(mx, my)){
                return true;
            }
        }
        return false;
    }
    private static boolean pertence(double x, double y, double R){
        for(int n=0; n<N; n++){
            if(ax(n)*x + ay(n)*y >= b(n)+R){
                return false;
            }
        }
        return true;
    }
    private static double ax(int i){
        return Math.cos(2*(2*i+1)*Math.PI/(2*N));
    }
    private static double ay(int i){
        return Math.sin(2*(2*i+1)*Math.PI/(2*N));
    }
    private static double b(int i){
        return B;
    }
}
