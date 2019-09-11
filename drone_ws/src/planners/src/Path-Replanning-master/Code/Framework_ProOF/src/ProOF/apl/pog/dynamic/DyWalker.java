/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.dynamic;

import ProOF.apl.pog.dynamic.DyWalker.State;
import ProOF.com.Linker.LinkerApproaches;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author marcio
 */
public class DyWalker extends aDyProblem<State, Double> {
    private static final int T = 20;
    private static final double dt = 0.5;
    
    //saindo de x=5, chegue em x=7 entre 3 e 5 segundos, espere de 1 a 2 segundos e termine o trajeto em x=0 depois de 0 a 5 segundos, todo trajeto deve terminar entre no 7 e 10 segundos 
    public class State extends DyState<State, Double>{
        private final double x;
        private final double s1;//chegada em x=7
        private final double s2;//saida   de x=7
        private final double s3;//chegada em x=0

        public State(State next, Double control, double x, double s1, double s2, double s3) {
            super(next, control);
            this.x = x;
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
        }
        @Override
        public boolean equals(Object obj) {
            State o = (State) obj;
            return Math.sqrt(Math.pow(o.x-x,2)+Math.pow(o.s1-s1,2)+Math.pow(o.s2-s2,2)+Math.pow(o.s3-s3,2)) < 0.1;
        }
        @Override
        public boolean feasible(){
            return -1<=x        && x <=11 &&
                    3<=s1       && s1<=5 &&
                    1<=s2       && s2<=2 &&
                    0<=s3       && s3<=5 &&
                    7<=s1+s2+s3 && s1+s2+s3<=10;
        }
        @Override
        public boolean equal(State o) throws Exception {
            return Math.abs(this.x-o.x)<0.1;
        }

        @Override
        public String toString() {
            return String.format("[%2s %15g %10g | %10g + %10g + %10g = %10g]", Math.abs(x-0)<0.5? "*" : Math.abs(x-7)<0.5? "#" : ""
                    ,x, control, s1/dt, s2/dt, s3/dt, (s1+s2+s3)/dt);
        }
        
    }

    private static LinkedList<LinkedList<State>> sols = null;
    private static LinkedList<Double> costs = null;
    private static LinkedList<State> bref = null;
    private static LinkedList<State> bcost = null;
    
    private static double best_ref = Double.POSITIVE_INFINITY;
    private static double best_cost = Double.POSITIVE_INFINITY;;
    
    private static JPanel panel = null;
    @Override
    public void add_plot(final LinkedList<State> sol, final double ref, final double cost) {
        if(sols==null){
            sols = new LinkedList<LinkedList<State>>();
            costs = new LinkedList<Double>();
            
            JFrame frame = new JFrame("plot");
            frame.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            panel = new JPanel(){
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(2));
                    g.drawRect(0, 0, getWidth()-1, getHeight()-1);
                    //g.drawRect(50, 50, 50, 50);
                    final int X0 = 50;
                    final int Y0 = 650;
                    final int W = 900;
                    final int H = 600;
                    g.drawLine(X0, Y0, X0+W, Y0);
                    for(int t=0; t<=T; t++){
                        g.drawLine(X0+t*W/T, Y0, X0+t*W/T, Y0-H);
                        g.drawString(String.format("%2d", t), X0+t*W/T-5, Y0+20);
                    }
                    Iterator<Double> it = costs.iterator();
                    for(LinkedList<State> sol : sols){
                        double cost = it.next();
                        g.setColor(new Color(255-Math.min((int)(best_cost*255/cost), 255), 255-Math.min((int)(best_cost*255/cost), 255), 255-Math.min((int)(best_cost*255/cost), 255)));
                        paint(g, sol, X0, Y0, W, H);
                    }
                    g2.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLUE);
                    paint(g, bref, X0, Y0, W, H);
                    
                    g.setColor(Color.GREEN);
                    paint(g, bcost, X0, Y0, W, H);
                }
                private void paint(Graphics g, LinkedList<State> sol, final int X0, final int Y0, final int W, final int H){
                    if(sol==null){
                        return;
                    }
                    Iterator<State> it = sol.iterator();
                    State a = it.next();
                    int t=0;
                    while(it.hasNext()){
                        State b = it.next();
                        g.drawLine(X0+t*W/T, (int)(Y0-H*a.x/10), X0+(t+1)*W/T, (int)(Y0-H*b.x/10));
                        a = b;
                        t++;
                    }
                }
            };
            panel.setBackground(Color.WHITE);
            panel.setPreferredSize(new Dimension(1000, 700));
            frame.add(panel);
            
            frame.setSize(1030, 760);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(ref<best_ref){
                    best_ref = ref;
                    bref = sol;
                }
                if(cost<best_cost){
                    best_cost = cost;
                    bcost = sol;
                }
                sols.addLast(sol);
                costs.addLast(cost);
                panel.repaint();
            }
        }); 
                
    }
    
    
    @Override
    public String name() {
        return "Dy-Walker";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
    }
    @Override
    public int T() throws Exception {
        return T;
    }
    @Override
    public State init_state(State s) throws Exception {
        return new State(null, null, 5.0, s.s1, s.s2, s.s3);
    }
    @Override
    public State last_states() throws Exception {
        double s1 = rmd.nextDouble(3, 5);
        double s2 = rmd.nextDouble(1, 2);
        double s3 = rmd.nextDouble(0, Math.min(5, 10-s1-s2));
        return new State(null, null, 0.0, s1, s2, s3);
    }
    @Override
    public Double control(State s) throws Exception {
        return rmd.nextDouble(-5, +5);
    }
    @Override
    public State back(State s, Double u) throws Exception {
        return new State(s, u, s.x-u, s.s1, s.s2, s.s3);
    }
    @Override
    public State next(State s, Double u) throws Exception {
        return new State(s, u, s.x+u, s.s1, s.s2, s.s3);
    }

    @Override
    public Double correction(Double u, State x, State r) throws Exception {
        u = u + -1*(r.x-x.x);
        u = Math.min(u, +5);
        u = Math.max(u, -5);
        return u;
    }
    
    
    @Override
    public double Cn(State s) throws Exception {
        if(Math.abs(s.x-0)<0.5 && s.feasible() ){
            return 0;
        }
        return Double.POSITIVE_INFINITY;
    }
    //minimizar combustivel
    @Override
    public double Ct(int t, State s, Double u) throws Exception{
        if( s.feasible() ){
            if( t*dt<=s.s1){
                return u*u;
            }else if( s.s1<=t*dt && t*dt<=s.s1+s.s2){
                return Math.abs(s.x-7)<0.5 ? u*u : Double.POSITIVE_INFINITY;
            }else if( s.s1+s.s2<=t*dt && t*dt<=s.s1 + s.s2 + s.s3){
                return u*u;
            }else if( s.s1 + s.s2 + s.s3<=t*dt){
                return Math.abs(s.x-0)<0.5 ? u*u : Double.POSITIVE_INFINITY;
            }else{
                return Double.POSITIVE_INFINITY;
            }
        }else{
            return Double.POSITIVE_INFINITY;
        }
    }
//    @Override
//    public double Ct(int t, State s, Double u) throws Exception{
//        if( s.feasible() ){
//            
//            if(Math.abs(s.x-0)<0.05){
//                if( s.s1 + s.s2 + s.s3<=t*dt){
//                    return u*u;
//                }else{
//                    return Double.POSITIVE_INFINITY;
//                }
//            }else if(Math.abs(s.x-7)<0.05){
//                if( s.s1<=t*dt && t*dt<=s.s1+s.s2){
//                    return u*u;
//                }else{
//                    return Double.POSITIVE_INFINITY;
//                }
//            }else{
//                return u*u;
//            }
//        }else{
//            return Double.POSITIVE_INFINITY;
//        }
//    }
    private static double pot = 1;
    @Override
    public double Hn(State s) throws Exception {
        return Math.pow(Math.abs(s.x - 7), pot);
    }
    @Override
    public double Ht(int t, State s, Double u) throws Exception {
        return t*dt>=s.s1+s.s2 ? Math.pow(Math.abs(s.x - 7),pot) :
               t*dt<=s.s1 ? Math.pow(Math.abs(s.x - 5),pot) : 0;
        //return 0;
    }
    
    
//    //minimizar tempo
//    @Override
//    public double Cn(State s) throws Exception {
//        if(Math.abs(s.x-0)<0.05 && s.feasible() ){
//            return s.s1 + s.s2 + s.s3;
//        }
//        return Double.POSITIVE_INFINITY;
//    }
//    @Override
//    public double Ct(int t, State s, Double u) throws Exception{
//        if( s.feasible() ){
//            if(Math.abs(s.x-0)<0.05){
//                if( s.s1 + s.s2 + s.s3<=t){
//                    return 0;
//                }else{
//                    return Double.POSITIVE_INFINITY;
//                }
//            }else if(Math.abs(s.x-7)<0.05){
//                if( s.s1<=t && t<=s.s1+s.s2){
//                    return 0;
//                }else{
//                    return Double.POSITIVE_INFINITY;
//                }
//            }else{
//                return 0;
//            }
//        }else{
//            return Double.POSITIVE_INFINITY;
//        }
//    }

    
}
