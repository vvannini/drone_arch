/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.BinaryReal;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinRealCrossOX extends oCrossover<Problem, cBinaryReal>{
    @Override
    public String name() {
        return "OX";
    }
    @Override
    public cBinaryReal crossover(Problem prob, cBinaryReal ind1, cBinaryReal ind2) throws Exception {
        cBinaryReal child = ind1.build(prob);
        for(int i=0; i<child.X.length; i++){
            for(int j=0; j<child.X[i].length; j++){
                child.X[i][j] = prob.rnd.nextBoolean() ? ind1.X[i][j] : ind2.X[i][j]; 
            }
        }
        return child;
    }
};
