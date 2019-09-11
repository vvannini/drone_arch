/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.apl.factorys.fProblem;
import ProOF.apl.factorys.fStop;
import ProOF.apl.pog.gen.codification.real.nCSV;
import ProOF.apl.pog.problem.PPDCP.Old.PPDCPInstanceOld;
import ProOF.com.Communication;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Stream.StreamPlot2D;
import ProOF.com.Linker.LinkerResults;
import ProOF.gen.codification.Real.cReal;
import ProOF.gen.operator.gCrossover;
import ProOF.gen.operator.gMutation;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oMutation;
import ProOF.gen.stopping.Stop;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.opt.abst.problem.meta.MultiProblem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.utilities.uSort;
import ProOF.utilities.uTournament;
import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;


/**
 *
 * @author marcio
 */
public class NSGAII extends MetaHeuristic{
    private int pop_size;
    private int tour_size;
    private double mut_rate;
    private MultiProblem problem;
    private Stop stop;
    private oInitialization init;
    private oCrossover[] cross;
    private oMutation[] mut;
    //private cIteration generations;
    
    private gCrossover op_cross;
    private gMutation op_mut;
    
    private StreamPlot2D ploter;
    
    private final nCSV call_back = nCSV.object();
    
    //private PPDCPInstanceOld inst;
    
    public NSGAII() {
        //generations = cIteration.object();
    }
    @Override
    public String name() {
        return "NSGA-II";
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        pop_size  = win.Int("population size",  100,  10, 10000);
        tour_size = win.Int("tournament size",    3,   2, 16   );
        mut_rate  = win.Dbl("mutation rate"  , 0.10,   0, 1    );
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(call_back);
        problem = com.get(fProblem.obj, MultiProblem.class, problem);
        stop    = com.get(fStop.obj, stop);
        init    = com.need(oInitialization.class, init);
        cross   = com.needs(oCrossover.class, new oCrossover[1]);
        mut     = com.needs(oMutation.class, new oMutation[1]);     
        
        //inst = com.need(PPDCPInstanceOld.class, inst);
    }
    
  
    
    private Sol R[];
    
    
    private Sol P(int i){
        return R[i];
    }
    private Sol Q(int i){
        return R[pop_size+i];
    }
    
    @Override
    public void execute() throws Exception {
        //Definie gerenciadores para os operadores
        op_cross = new gCrossover(problem, cross);
        op_mut = new gMutation(problem, mut);
        
        //Declara a população e faz alocação de mémoria
        R = new Sol[pop_size*2];
        for(int i=0; i<R.length; i++){
            R[i] = new Sol();
        }
        
        //Inicia e avalia P0
        for(int i=0; i<pop_size; i++){
            P(i).new_sol();
            P(i).initialize();
            P(i).evaluate();
        }
        
        //Cria o gerenciado de seleção
        uTournament tour = new uTournament(R, tour_size);
        
        do{
            //Gera e avalia Qt a partir de Pt
            for(int i=0; i<pop_size; i++){
                int p1 = tour.select_in(pop_size);
                int p2 = tour.select_in(pop_size);
                
                //Crossover
                Q(i).crossover(P(p1), P(p2));
                
                //Mutação
                if(Math.random() < mut_rate){
                    Q(i).mutation();
                }
                
                //Avaliação
                Q(i).evaluate();
            }
            
            Integer ranks[][] = fast_nondominated_sorting();
            for(Integer[] I : ranks){
                crowding_distance_assignment(I);
            }
            //Make next population
            goal = GOAL_ALL;
            uSort.sort(R);
            
            if(problem.goals()==2){
                
                //inst.clear();
                for(int i=0; i<pop_size; i++){
                    double x = problem.goal(0, P(i).sol);
                    double y = problem.goal(1, P(i).sol);
                    Color color;
                    if(P(i).rank==1){
                        color = Color.RED;
                        /*if(P(i).sol.codif() instanceof cReal){
                            inst.plot(Mt(inst, (cReal)P(i).sol.codif()));
                        }*/
                    }else if(P(i).rank==2){
                        color = Color.GREEN;
                    }else if(P(i).rank==3){
                        color = Color.BLUE;
                    }else{
                        color = Color.darkGray;
                    }
                    ploter.point(i, x, y, color);
                }
                /*for(int i=0; i<R.length; i++){
                    double x = problem.goal(0, R[i].sol);
                    double y = problem.goal(1, R[i].sol);
                    Color color;
                    if(R[i].rank==1){
                        color = Color.RED;
                    }else if(R[i].rank==2){
                        color = Color.GREEN;
                    }else if(R[i].rank==3){
                        color = Color.BLUE;
                    }else{
                        color = Color.darkGray;
                    }
                    ploter.point(i, x, y, color);
                }*/
                Thread.sleep(100);
            }
        }while(!stop.end());
    }

    public static double Ut(cReal codif, int t, int j){
        return codif.X(t*2+j, -3, 3);
    }
    public static double[][] Mt(PPDCPInstanceOld inst, cReal codif){
        double exp[][] = new double[inst.T][4];
        //-------------------------------- (11) --------------------------------
        for(int t=0; t<inst.T; t++){
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        exp[t][k] += R[k][j] * Ut(codif,i,j);
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t+1), inst.X0);
            for(int k=0; k<4; k++){
                exp[t][k] += AX0[k][0];
            }
        }
        return exp;
    }
    
    private Integer[][] fast_nondominated_sorting(){
        LinkedList<LinkedList<Integer>> ranks = new LinkedList<LinkedList<Integer>>();
        
        
        LinkedList<Integer>[] Sp = new LinkedList[R.length];
        int[] Np = new int[R.length];
        LinkedList<Integer> Fi = new LinkedList<Integer>();
        
        for(int p=0; p<R.length; p++){
            Sp[p] = new LinkedList<Integer>();
            Np[p] = 0;
            for(int q=0; q<R.length; q++){
                if(R[p].dominates(R[q])){
                    Sp[p].addLast(q);
                }else if(R[q].dominates(R[p])){
                    Np[p]++;
                }
            }
            if(Np[p]==0){
                R[p].rank = 1;
                Fi.addLast(p);
            }
        }
        ranks.addLast(Fi);
        
        int i=1;
        while(Fi.size()>0){
            LinkedList<Integer> Q = new LinkedList<Integer>();
            for(int p : Fi){
                for(int q : Sp[p]){
                    Np[q]--;
                    if(Np[q]==0){
                        R[q].rank = i+1;
                        Q.addLast(q);
                    }
                }
            }
            i++;
            Fi = Q;
            if(Fi.size()>0){
                ranks.addLast(Fi);
            }
        }
        Integer array[][] = new Integer[ranks.size()][];
        int j=0;
        for(LinkedList<Integer> rank : ranks){
            array[j] = rank.toArray(new Integer[rank.size()]);
            j++;
        }
        return array;
    }
    private void crowding_distance_assignment(Integer I[]) throws Exception{
        final int l = I.length-1;
        for(int i : I){
            R[i].distance = 0;
        }
        for(int m=0; m<problem.goals(); m++){
            
            sort(I, m);
            
            for(int i=1; i<l; i++){
                double goal_A = problem.goal(m, R[I[i-1]].sol);
                double goal_B = problem.goal(m, R[I[i+1]].sol);
                
                R[I[i]].distance += (goal_B - goal_A) / (MAX[m] - MIN[m]);
            }
            
            R[I[0]].distance += Integer.MAX_VALUE;
            R[I[l]].distance += Integer.MAX_VALUE;
        }
    }
    private void sort(Integer I[], int m){
        goal = m;
        uSort.sort(R, I);
        goal = GOAL_ALL;
    }
    
    @Override
    public void results(LinkerResults win) throws Exception {
        //win.writeLong("generations", generations.value());
        Integer ranks[][] = fast_nondominated_sorting();
        for(Integer[] I : ranks){
            crowding_distance_assignment(I);
        }
        //Make next population
        goal = GOAL_ALL;
        uSort.sort(R);

        File file = new File("./pateto.csv");
        PrintStream out = new PrintStream(file);
        
        //Title
        out.printf("%s;%s;%s;", "index", "rank", "distance");
        call_back.title(out, R[0].sol);
        out.println();
        
        //Values
        for(int i=0; i<R.length; i++){
            out.printf("%d;%d;%g;", i, R[i].rank, R[i].distance);
            call_back.values(out, R[i].sol);
            out.println();    
        }
        out.close();
        
        win.writeFile("pareto",file);
    }
    
    private static final int GOAL_ALL = -1;
    private int goal;
    private double MAX[];
    private double MIN[];

    @Override
    public void start() throws Exception {
        goal = GOAL_ALL;
        MAX = new double[problem.goals()];
        MIN = new double[problem.goals()];
        for(int m=0; m<problem.goals(); m++){
            MAX[m] = Integer.MIN_VALUE;
            MIN[m] = Integer.MAX_VALUE;
        }
        if(problem.goals()==2){
            ploter = Communication.mkPlot2D("NSGA-II plot2D");
            ploter.background(Color.WHITE);
        }
    }
    
    private class Sol implements Comparable<Sol>{
        private Solution sol;
        private int rank;
        private double distance;    //crowding-distance
        
        public Sol() {
            this.sol = null;
            this.rank = -1;
            this.distance = -1;
        }
        @Override
        public int compareTo(Sol o) {
            if(goal==GOAL_ALL){
                if(rank != o.rank){
                    return Integer.compare(rank, o.rank);
                }else{
                    return Double.compare(o.distance, distance);
                }
            }else{
                return problem.compareTo(goal, this.sol, o.sol);
            }
        }
        private boolean dominates(Sol o) {
            return sol.compareTo(o.sol) < 0;
        }

        private void new_sol() throws Exception {
            sol = problem.build_sol();   
        }
        private void initialize() throws Exception {
            init.initialize(problem, sol.codif());
        }
        private void evaluate() throws Exception {
            problem.evaluate(sol);
            for(int m=0; m<problem.goals(); m++){
                double goal = problem.goal(m, sol);
                MAX[m] = Math.max(MAX[m], goal);
                MIN[m] = Math.min(MIN[m], goal);
            }
        }                
        private void crossover(Sol P1, Sol P2) throws Exception {
            sol = op_cross.crossover(P1.sol, P2.sol);
        }
        private void mutation() throws Exception {
            op_mut.mutation(sol);
        }
    }
}
