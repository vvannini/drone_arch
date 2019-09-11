/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;

import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public class BMOperator extends Factory<Operator>{
    public static final BMOperator obj = new BMOperator();

    @Override
    public String name() {
        return "PPDCP Operators";
    }
    
    @Override
    public Operator build(int index) {
        switch(index){
            case  0: return new Init();
        }
        return null;
    }
    
    private class Init extends oInitialization<BMProblem, BMCodification>{
        @Override
        public String name() {
            return "Init";
        }
        @Override
        public void initialize(BMProblem prob, BMCodification ind) throws Exception {
            for(int t=0; t<ind.Ut.length; t++){
                ind.Ut[t] = prob.serv.build_control(prob);
            }
            //ind.Xt[0] = prob.serv.state_begin();
            
        }
    }
    private class Cross extends oCrossover<BMProblem, BMCodification>{
        @Override
        public String name() {
            return "Cross";
        }
        @Override
        public BMCodification crossover(BMProblem prob, BMCodification ind1, BMCodification ind2) throws Exception {
            BMCodification child = ind1.build(prob);
            for(int t=0; t<ind1.Ut.length; t++){
                child.Ut[t] = prob.rnd.nextBoolean() ? ind1.Ut[t] : ind2.Ut[t];
            }
            return child;
        }
    }
}
