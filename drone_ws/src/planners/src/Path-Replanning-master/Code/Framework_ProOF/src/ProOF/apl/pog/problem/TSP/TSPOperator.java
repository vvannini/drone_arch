/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.TSP;

import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Operator;
import java.util.Random;

/**
 *
 * @author marcio
 */
public class TSPOperator extends Factory<Operator>{
    public static final TSPOperator obj = new TSPOperator();
    
    @Override
    public String name() {
        return "TSP Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new INIT();
            case 1: return new MUT();
            case 2: return new CROSS();
            case 3: return new MOV();
        }
        return null;
    }
    private class INIT extends oInitialization<Problem, TSPCodification>{
        @Override
        public String name() {
            return "Random";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo hamiltoniano";
        }
        @Override
        public void initialize(Problem mem, TSPCodification ind) throws Exception {
            for(int i=0; i<ind.path.length; i++){
                ind.path[i] = i;
            }
            for(int i=0; i<ind.path.length; i++){
                SwapRandom(mem.rnd, ind);
            }
        }

        
    }
    private class MOV extends oLocalMove<Problem, TSPCodification>{
        @Override
        public void local_search(Problem mem, TSPCodification ind) throws Exception {
            SwapRandom(mem.rnd, ind);
        }

        @Override
        public String name() {
            return "Mov-SwapCities";
        }
    }
    private class MUT extends oMutation<Problem, TSPCodification>{
        @Override
        public void mutation(Problem mem, TSPCodification ind) throws Exception {
            SwapRandom(mem.rnd, ind);
        }
        @Override
        public String name() {
            return "Mut-SwapCities";
        }
    }
    private class CROSS extends oCrossover<TSPFactory, TSPCodification>{
        @Override
        public String name() {
            return "TwoPoints";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo hamiltoniano";
        }
        @Override
        public TSPCodification crossover(TSPFactory tsp, TSPCodification ind1, TSPCodification ind2) throws Exception {
            TSPCodification child = ind1.build(tsp);
            int p[] = tsp.rnd.cuts_points(tsp.inst.N, 2);
            boolean selected[] = new boolean[tsp.inst.N];
            for(int i=p[0]; i<p[1]; i++){
                child.path[i] = ind1.path[i];
                selected[child.path[i]] = true;
            }
            int i=p[1];
            int j=p[1];
            while(i!=p[0]){
                while(selected[ind2.path[j]]){
                    j = (j+1) % tsp.inst.N;
                }
                child.path[i] = ind2.path[j];
                selected[child.path[i]] = true;
                i = (i+1) % tsp.inst.N;
            }
            return child;
        }
    }
    private static void SwapRandom(Random rmd, TSPCodification ind){
        int a =  rmd.nextInt(ind.path.length);
        int b =  rmd.nextInt(ind.path.length);
        int aux = ind.path[a];
        ind.path[a] = ind.path[b];
        ind.path[b] = aux;
    }
}
