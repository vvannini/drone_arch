/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Integer;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iIntCrossOX extends oCrossover<Problem, cInteger>{
    @Override
    public String name() {
        return "OX";
    }
    @Override
    public cInteger crossover(Problem prob, cInteger ind1, cInteger ind2) throws Exception {
        cInteger child = ind1.build(prob);
        for(int i=0; i<child.cromo.length; i++){
            child.cromo[i] = prob.rnd.nextBoolean() ? ind1.cromo[i] : ind2.cromo[i]; 
        }
        return child;
    }
};
