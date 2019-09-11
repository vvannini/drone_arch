/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.utilities.uUtil;

/**
 *
 * @author marcio
 */
public class iRealCrossOXFast extends oCrossover<Problem, cReal>{
    private long mask[] = null;
    @Override
    public String name() {
        return "OX-Fast";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        mask = prob.rnd.nextMask(mask, child.X.length);
        for(int i=0; i<child.X.length; i++){
            child.X[i] = uUtil.decode(mask, i, ind1.X[i], ind2.X[i]);
        }
        return child;
    }
};
