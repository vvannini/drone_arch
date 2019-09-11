/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MLCLSP;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.operator.oFixAndOpt;
import ProOF.gen.operator.oHcGA;

/**
 *
 * @author marcio
 */
public class MLCLSPOperator extends Factory<Operator>{
    public static final MLCLSPOperator obj = new MLCLSPOperator();
    
    @Override
    public String name() {
        return "MLCLSP Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new HcGAOperator();
            case 1: return new FIXAndOPT();
        }
        return null;
    }
    private class HcGAOperator extends oHcGA<MLCLSPProblem, Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution>>{
        protected double probability;
        protected double P[][];
        
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            probability = link.Dbl("initial probability", 0.9, 0, 1);
        }
        
        @Override
        public String name() {
            return "update";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo hamiltoniano";
        }
     
        @Override
        public void initialize(MLCLSPProblem prob) throws Exception {
            P = new double[prob.inst.J][prob.inst.T];
            for(int j=0; j<prob.inst.J; j++){
                for(int t=0; t<prob.inst.T; t++){
                    P[j][t] = probability;
                }
            }
        }
        @Override
        public void create(MLCLSPProblem prob, Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution> ind) throws Exception {
            //Create individual with probability matrix P;
            for(int j=0; j<prob.inst.J; j++){
                for(int t=0; t<prob.inst.T; t++){
                    if(prob.rnd.nextDouble() < P[j][t]){
                        ind.codif().Y[j][t] = 1;
                    }else{
                        ind.codif().Y[j][t] = 0;
                    }
                }
            }
        }
        @Override
        public void update(MLCLSPProblem prob, Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution> ind, int popSize) throws Exception {
            //Update probability matrix;
            for(int j=0; j<prob.inst.J; j++){
                for(int t=0; t<prob.inst.T; t++){
                    if(ind.codif().Y[j][t]==1){
                        P[j][t] = Math.min(0.999, P[j][t] + 1.0/popSize);
                    }else{
                        P[j][t] = Math.max(0.001, P[j][t] - 1.0/popSize);
                    }
                }
            }
        }
    }
    private class FIXAndOPT extends oFixAndOpt<MLCLSPProblem, Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution>>{
        protected int per_wdw;
        protected int prod_wdw;
        
        @Override
        public String name() {
            return "Fix & Opt";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo hamiltoniano";
        }
        public void initialize(MLCLSPProblem prob) throws Exception {
            this.per_wdw = 1;
            this.prod_wdw = 1;
        }
        @Override
        public Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution> execute(MLCLSPProblem prob, Solution<MLCLSPProblem, MLCLSPObjective, MLCLSPCodification, Solution> ind) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        public void increment(MLCLSPProblem prob) throws Exception{
            this.per_wdw++;
            this.prod_wdw++;
        }
    }
    
}
