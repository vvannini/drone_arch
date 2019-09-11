/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iRealCrossOX extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "OX";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        for(int i=0; i<child.X.length; i++){
            child.X[i] = prob.rnd.nextBoolean() ? ind1.X[i] : ind2.X[i]; 
        }
        return child;
    }
};
