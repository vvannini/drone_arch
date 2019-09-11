/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.BranchAndBound;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloNumVar;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class iCCQSP extends aOptimalProblem{
    
    private static final double dt = 1.0;
    private static final double w = 0.01;   //sigma
    private static final double M = Integer.MAX_VALUE;
    private static final double A[][] = new double[][]{
        {   1, 0,  dt, 0   },
        {   0, 1,  0,  dt  },
        {   0, 0,  1,  0   },
        {   0, 0,  0,  1   },
    };
    private static final double B[][] = new double[][]{
        {   dt*dt/2,    0       },
        {   0,          dt*dt/2 },
        {   dt,         0       },
        {   0,          dt      },
    };
    private static final double W[][] = new double[][]{
        {   w*w,       0       },
        {   0,          w*w    },
        {   0,          0       },
        {   0,          0       },
    };
    private static final double graph[][] = new double[][]{
        {   M,      4.1,    5.5 },
        {   0.8,    M,      3.8 },
        {   0.0,    1.6,    M   },
    };

    

   

    
    
    private class CCQSP{
        private final double max_time;
        
        public CCQSP(double matrix[][]) {
            this.max_time = dijkstra(matrix, 0, events());
        }
        private final int N(){ //T = [0, 1, ... , N] ;  max-real-time = N * dt 
            return (int)(max_time/dt() + 0.001);
        }
        
        private double dt(){    //time discretization
            return dt;
        }
        private int states(){   //px, py, vx, vy
            return 4;
        }
        private int inputs(){   //ax, ay
            return 2;
        }
        private double[][] A(){
            return A;
        }
        private double[][] B(){
            return B;
        }
        private double[][] W(){
            return W;
        }
        
        private int events(){   //e = [e0, e1, ..., eE]
            return 2;
        }
        private double[][] graph(){
            return graph;
        }
        private boolean non_convex_episodes(int a, int b){  //convex region is in all episodes in which the events are of e0 to eE
            return 0<=a && b<=events();
            // (0<=a && b<=1) || (2<=a && b<=3)
            // (1<=a && b<=2)
        }
    }
    
    
    private final double dijkstra(double matrix[][], int start, int end){
        return matrix[start][end];  //not implemented yet, so this return is not optimal but is feasible
    }
    
    /**
     * Problem 9: Relaxed Optimization Problem for a Partial Schedule
     */
    private class ROPPS{
        private CplexExtended cpx;
        private IloNumVar U[][] = new IloNumVar[ccqsp.N()-1][ccqsp.inputs()];
        private IloNumVar X[][] = new IloNumVar[ccqsp.N()][ccqsp.states()];;
        
        
        private void initialize(LinkedList<Integer> partial_schedule){
            //TODO custo parcial resolver o problema 9
            throw new UnsupportedOperationException("Not supported yet.");  //solve the problem 9
            
        }
        private boolean solve() throws Exception{
            return cpx.solve();
            /*if(cpx.solve()){
                return cpx.getObjValue();
            }else{
                return Integer.MAX_VALUE;
            }*/
        }
    }
    
    private double obtainLowerBound(Event event, LinkedList<Integer> partial_schedule) throws Exception{
        //solve sub-problem 9
        ropps.initialize(partial_schedule);
        ropps.solve();
        if(event.is_integer() || non_convex(partial_schedule)){
            return NIRA(event, partial_schedule);
        }else{
            return obtainLowerBoundFRR(event, partial_schedule);
        }
    }
    private boolean non_convex(LinkedList<Integer> partial_schedule) {
        for(int p=0; p<partial_schedule.size(); p++){
            if(ccqsp.non_convex_episodes(p, p)){
                return true;
            }
        }
        return false;
    }
    private double NIRA(Event event, LinkedList<Integer> partial_schedule) {
        //TODO algorithm 1
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private double obtainLowerBoundFRR(Event event, LinkedList<Integer> partial_schedule) {
        //TODO algorithm 5
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private CCQSP ccqsp = new CCQSP(graph);
    private ROPPS ropps = new ROPPS();
    
    @Override
    public String name() {
        return "CCQSP";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    
    private class TimeEvent{
        private final int time;
        private final double d_graph[][];

        public TimeEvent(int time, double[][] d_graph) {
            this.time = time;
            this.d_graph = new double[d_graph.length][d_graph.length];
            for(int i=0; i<d_graph.length; i++){
                System.arraycopy(d_graph[i], 0, this.d_graph[i], 0, d_graph.length);
            }
            //TODO fazer atualizacao do d-graph utilizando o shortest path algorithm
        }

        /*@Override
        public boolean equals(Object obj) {
            return ((TimeEvent)obj).time==time;
        }*/
        @Override
        public String toString() {
            return String.format("%d", time);
        }
        protected int mem_bytes() {
            return (4 + d_graph.length*d_graph.length);
        }
    }
    
    private class Event extends aOptimalProblem.Node<Event, TimeEvent> {
        
        public Event(Event back, TimeEvent time, double uper_bound) throws Exception {
            super(back, time, uper_bound);
        }
        @Override
        protected boolean is_integer() {
            return level>=ccqsp.events();
        }
        @Override
        protected double partial_cost() throws Exception {
            LinkedList<Integer> partial_schedule = new LinkedList<Integer>();
            Event aux = this;
            while(aux!=null){
                partial_schedule.addFirst(aux.data.time);
                aux = aux.back;
            }
            return obtainLowerBound(this, partial_schedule);
        }
        @Override
        protected double integer_cost() throws Exception {
            return partial_cost();
        }
        @Override
        protected double lower_bound(double uper_bound) {
            return cur_cost;
        }
        @Override
        protected int mem_bytes() {
            return mem_base() + data.mem_bytes();
        }

        @Override
        protected Event[] childs(double uper_bound) throws Exception{
            LinkedList<Event> list = new LinkedList<Event>();
            final int ti = this.data.time + (int)(this.data.d_graph[this.level+1][this.level]/ccqsp.dt() + 0.999);
            final int tf = this.data.time + (int)(this.data.d_graph[this.level][this.level+1]/ccqsp.dt() + 0.001);
            for(int t=ti; t<=tf; t++){
                list.addLast(new Event(this, new TimeEvent(t, data.d_graph), uper_bound));
            }
            return list.toArray(new Event[list.size()]);
        }
    }
    @Override
    public Node frist_node(double uper_bound) throws Exception{
        return new Event(null, new TimeEvent(0, graph), uper_bound);
    }
    @Override
    public void call_end(Node best_integer) {
        Node aux = best_integer;
        while(aux!=null){
            System.out.printf("%s <- ", aux.data);
            aux = aux.back;
        }
        System.out.println();
        
        aux = best_integer;
        while(aux!=null){
            System.out.printf("%g <- ", aux.lower);
            aux = aux.back;
        }
        System.out.println();
    }
}
