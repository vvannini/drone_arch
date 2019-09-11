/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Binary;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinCrossOX extends oCrossover<Problem, cBinary>{
    @Override
    public String name() {
        return "OX";
    }
    @Override
    public cBinary crossover(Problem prob, cBinary ind1, cBinary ind2) throws Exception {
        cBinary child = ind1.build(prob);
        for(int i=0; i<child.cromo.length; i++){
            child.cromo[i] = prob.rnd.nextBoolean() ? ind1.cromo[i] : ind2.cromo[i]; 
        }
        return child;
    }
};
