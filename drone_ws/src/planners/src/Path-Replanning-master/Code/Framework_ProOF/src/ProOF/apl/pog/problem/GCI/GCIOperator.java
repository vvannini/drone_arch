/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public class GCIOperator extends Factory<Operator>{
    public static final GCIOperator obj = new GCIOperator();
    
    @Override
    public String name() {
        return "GCI Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0:  return new GCIOperator.initRMD();
            
            case 1:  return new GCIOperator.movChangeOne();
            case 2:  return new GCIOperator.movChangeTwo();
            case 3:  return new GCIOperator.movSwapProducts();
            case 4:  return new GCIOperator.movSwapInK();
            case 5:  return new GCIOperator.movSwapInT();
            case 6:  return new GCIOperator.movFurnanceED();
            
            case 7:  return new GCIOperator.mutChangeOne();
            case 8:  return new GCIOperator.mutChangeTwo();
            case 9:  return new GCIOperator.mutSwapProducts();
            case 10: return new GCIOperator.mutSwapInK();
            case 11: return new GCIOperator.mutSwapInT();
            case 12: return new GCIOperator.mutFurnanceED();
            
            case 13: return new GCIOperator.crossOXrmd();
            case 14: return new GCIOperator.cross1PTrmd();
            case 15: return new GCIOperator.cross1PKrmd();
            case 16: return new GCIOperator.crossOXmin();
            case 17: return new GCIOperator.cross1PTmin();
            case 18: return new GCIOperator.cross1PKmin();   
        }//return new String[]{"OX_rmd","1P_rmd-t","1P_rmd-k", "OX_min","1P_min-t","1P_min-k"};
        return null;
    }
    private class initRMD extends oInitialization<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "A(k , t) = i*";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente uma sequencia de producao";
        }
        @Override
        public void initialize(GCIFactory prob, GCICodification ind) throws Exception {
            for(int k=0; k<prob.inst.K; k++){
                for(int t=0; t<prob.inst.T; t++){
                    int index = prob.rnd.nextInt(prob.inst.Productkn[k].length);
                    ind.cromo[k][t] = prob.inst.Productkn[k][index];
                }
            }
            ind.te = prob.inst.T;
        }
    }
    
    private class movChangeOne extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Change One";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
            ind.ChangeOneProduct(mem);
        }
    }
    private class movChangeTwo extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Change Two";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
           ind.ChangeTwoProduct(mem);
        }
    }
    private class movSwapProducts extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap Products";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProduct(mem);
        }
    }
    private class movSwapInK extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap in K";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProductIn_K(mem);
        }
    }
    private class movSwapInT extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap in T";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProductIn_T(mem);
        }
    }
    private class movFurnanceED extends oLocalMove<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Furnance E/D";
        }
        @Override
        public void local_search(GCIFactory mem, GCICodification ind) throws Exception {
            ind.DisableEnableFurnance(mem);
        }
    }
    
    private class mutChangeOne extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Change One";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
            ind.ChangeOneProduct(mem);
        }
    }
    private class mutChangeTwo extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Change Two";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
           ind.ChangeTwoProduct(mem);
        }
    }
    private class mutSwapProducts extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap Products";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProduct(mem);
        }
    }
    private class mutSwapInK extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap in K";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProductIn_K(mem);
        }
    }
    private class mutSwapInT extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Swap in T";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
            ind.SwapTwoProductIn_T(mem);
        }
    }
    private class mutFurnanceED extends oMutation<GCIFactory, GCICodification>{
        @Override
        public String name() {
            return "Furnance E/D";
        }
        @Override
        public void mutation(GCIFactory mem, GCICodification ind) throws Exception {
            ind.DisableEnableFurnance(mem);
        }
    }
    
    
    
    private class crossOXrmd extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "OX-rmd";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.CrossoverOX(ind2, prob, true);
        }
    }
    private class cross1PTrmd extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "1PT-rmd";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.Crossover1P_t(ind2, prob, true);
        }
    }
    private class cross1PKrmd extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "1PK-rmd";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.Crossover1P_k(ind2, prob, true);
        }
    }
    private class crossOXmin extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "OX-min";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.CrossoverOX(ind2, prob, false);
        }
    }
    private class cross1PTmin extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "1PT-min";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.Crossover1P_t(ind2, prob, false);
        }
    }
    private class cross1PKmin extends oCrossover<GCIFactory, GCICodification> {
        @Override
        public String name() {
            return "1PK-min";
        }
        @Override
        public GCICodification crossover(GCIFactory prob, GCICodification ind1, GCICodification ind2) throws Exception {
            return ind1.Crossover1P_k(ind2, prob, false);
        }
    }
}
