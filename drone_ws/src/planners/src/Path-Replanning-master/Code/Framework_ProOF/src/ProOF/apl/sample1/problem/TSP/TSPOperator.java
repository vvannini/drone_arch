/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.sample1.problem.TSP;

import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
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
    public Operator build(int index) {  //build the operators
        switch(index){
            case 0: return new RandomTour();    //initialization
            case 1: return new MutExchange();   //mutation
            case 2: return new TwoPoints();     //crossover
            case 3: return new MovExchange();   //local movement
        }
        return null;
    }
    
    private class RandomTour extends oInitialization<TSP, cTSP>{
        @Override
        public String name() {
            return "Random Tour";
        }
        @Override
        public void initialize(TSP prob, cTSP ind) throws Exception {
            for(int i=0; i<ind.path.length; i++){
                ind.path[i] = i;
            }
            for(int i=0; i<ind.path.length; i++){
                random_swap(prob.rnd, ind);
            }
        }
    }
    private class MutExchange extends oMutation<TSP, cTSP>{
        @Override
        public String name() {
            return "Mut-Exchange";
        }
        @Override
        public void mutation(TSP prob, cTSP ind) throws Exception {
            random_swap(prob.rnd, ind);
        }
    }
    private class MovExchange extends oLocalMove<TSP, cTSP>{
        @Override
        public String name() {
            return "Mov-Exchange";
        }
        @Override
        public void local_search(TSP prob, cTSP ind) throws Exception {
            random_swap(prob.rnd, ind);
        }
    }
    private class TwoPoints extends oCrossover<TSP, cTSP>{
        @Override
        public String name() {
            return "TwoPoints";
        }
        @Override
        public cTSP crossover(TSP prob, cTSP ind1, cTSP ind2) throws Exception {
            cTSP child = ind1.build(prob);
            int p[] = prob.rnd.cuts_points(prob.inst.N, 2);
            boolean selected[] = new boolean[prob.inst.N];
            for(int i=p[0]; i<p[1]; i++){
                child.path[i] = ind1.path[i];
                selected[child.path[i]] = true;
            }
            int i=p[1];
            int j=p[1];
            while(i!=p[0]){
                while(selected[ind2.path[j]]){
                    j = (j+1) % prob.inst.N;
                }
                child.path[i] = ind2.path[j];
                selected[child.path[i]] = true;
                i = (i+1) % prob.inst.N;
            }
            return child;
        }
    }
    
    private static void random_swap(Random rmd, cTSP ind){
        int a =  rmd.nextInt(ind.path.length);
        int b =  rmd.nextInt(ind.path.length);
        int aux = ind.path[a];
        ind.path[a] = ind.path[b];
        ind.path[b] = aux;
    }
}
