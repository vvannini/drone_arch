/*
 * DPOC - DINAMIC PROGRAMING OPTIMAL CONTROL ALGORITHM
 */
package ProOF.apl.pog.method.DPOC;


import ProOF.apl.factorys.fDynamicProblem;
import ProOF.apl.factorys.fStop;
import ProOF.apl.pog.dynamic.DyState;
import ProOF.apl.pog.dynamic.aDyProblem;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.gen.stopping.Stop;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class DPOC extends Run {

    //private aStop stop;
    private aDyProblem problem;
    //private oDynamic dynamic;
    
    private long N; //number of particles
    private int tour;
    @Override
    public String name() {
        return "DPOC";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        problem = link.get(fDynamicProblem.obj, problem);
        //stop    = link.get(fStop.obj, stop);
        //dynamic = link.need(oDynamic.class, dynamic);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        //N       = link.Long("Nº particles", 1000, 10, 1000000000000L);
        //tour    = link.Int("tour", 3, 1, 100);
        
        N = 10000;
        tour = 3;
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        
    }
    
    public static int compare(double a, double b) {
        if(a>b+1e-3){
            return +1;
        }else if(a<b-1e-3){
            return -1;
        }else{
            return 0;
        }
    }
    private class Node implements Comparable<Node> {
        private final DyState state;
        private final Object control;
        private final double cost;
        private final double heur;
        
        private Node() throws Exception {
            this.state = problem.last_states();
            this.control = null;
            this.cost = problem.Cn(state);
            this.heur = this.cost + problem.Hn(state);
        }

        private Node(int t, DyState state, Object control, double sum_cost) throws Exception {
            this.state = state;
            this.control = control;
            this.cost = sum_cost + problem.Ct(t, state, control);
            this.heur = this.cost+problem.Ht(t, state, control);
        }
        @Override
        public int compareTo(Node o) {
            return compare(this.cost, o.cost);
        }
        public int compareHeur(Node o) {
            return compare(this.heur, o.heur);
        }
    }
    
    public Node tournament(ArrayList<Node> array, int tour_size){
        int best = problem.rmd.nextInt(array.size());
        for(int j=1; j<tour_size; j++){
            int i = problem.rmd.nextInt(array.size());
            if(array.get(i).compareTo(array.get(best)) < 0){
                best = i;
            }
        }
        return array.get(best);
    }
    
    @Override
    public void execute() throws Exception {
        ArrayList<Node>[] array = new ArrayList[problem.T()+1];
        for(int t=0; t<array.length; t++){
            array[t] = new ArrayList<Node>();
        }
        
        //for each state calc Vt
        int t = problem.T();
        while(array[t].size()<N){
            Node node = new Node();
            if(node.state.feasible()){
                array[t].add(node);
            }
        }
        t--;
        
        while(t>=0){
            //for each state and best control calc Vt
            //System.out.println("-------------------- ["+t+"] -----------------------");
            while(array[t].size()<N){
                Node Vn = tournament(array[t+1], tour);
                for(int j=0; j<tour; j++){
                    Object u = problem.control(Vn.state);
                    DyState Vt = problem.back(Vn.state, u);
                    if(Vt.feasible()){
                        array[t].add(new Node(t, Vt, u, Vn.cost));
                        //System.out.printf("[%d] %s + %10g -> %s # %10g %10g\n",j, Vt, u, Vn.state, Vn.cost, Vn.heur);
                    }
                }
            }
            t--;
        }
        
        
        LinkedList<DyState> best = null;
        double real_cost = Double.POSITIVE_INFINITY;
        double ref_cost = Double.POSITIVE_INFINITY;
        double ref_heur = Double.POSITIVE_INFINITY;
        double best_cost = Double.POSITIVE_INFINITY;
        double best_heur = Double.POSITIVE_INFINITY;
        
        DyState ref = null;
        for(Node n : array[0]){
            if(!Double.isInfinite(n.cost)){
                LinkedList<DyState> sol = new LinkedList<DyState>();
                double cost = 0;
                DyState init = problem.init_state(n.state);
                //System.out.println("ref = "+n.cost);
                DyState r = init;
                DyState x = n.state;
                //System.out.println("-------------------- sol -----------------------");
                //System.out.println("["+0+"]"+r+"   "+x + " cost = "+ n.cost);
                
                sol.addLast(r);
                for(t=0; t<problem.T() && !Double.isInfinite(cost); t++){
                    Object u = problem.correction(x.control, x, r);
                    //System.out.println("u["+t+"] = "+u);
                    cost += problem.Ct(t, r, u);
                    r = problem.next(r, u);
                    x = x.next;
                    sol.addLast(r);
                    
                    //System.out.println("["+(t+1)+"]"+r+"   "+x + "   "+u);
                }
                
                
                //System.out.println("cost1 = "+cost);
                if(!Double.isInfinite(cost)){
                    cost += problem.Cn(r);
                }
                if(!Double.isInfinite(cost)){
                    problem.add_plot(sol, n.cost, cost);
                }
                //System.out.println("cost2 = "+cost);
                
                if(!Double.isInfinite(cost) && cost<real_cost){
                    real_cost = cost;
                    ref_cost = n.cost;
                    ref_heur = n.heur;
                    ref = n.state;
                    best = sol;
                }
                if(!Double.isInfinite(n.cost) && n.cost<best_cost){
                    best_cost = n.cost;
                }
                if(!Double.isInfinite(n.heur) && n.heur<best_heur){
                    best_heur = n.heur;
                }
            }
        }
        if(best!=null){
            System.out.println("-------------------- best -----------------------");
            System.out.println("real        = "+real_cost);
            System.out.println("ref         = "+ref_cost);
            System.out.println("heur        = "+ref_heur);
            System.out.println("best-cost   = "+best_cost);
            System.out.println("best-heur   = "+best_heur);
            
            t = 0;
            for(DyState s : best){
                System.out.printf("[%2d] %s\n", t++, s);
            }
            System.out.println("-------------------- ref -----------------------");
            for(t=0; t<problem.T(); t++){
                System.out.printf("[%2d] %s\n", t, ref);
                ref = ref.next;
            }
        }else{
            System.out.println("Infeasible");
        }
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
}
