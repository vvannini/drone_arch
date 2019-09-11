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
public class iRealCrossOnePoint extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "One-point";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        int tc = prob.rnd.nextInt(child.X.length);
        System.arraycopy(ind1.X, 0, child.X, 0, tc);                    //[0..tc-1]
        System.arraycopy(ind2.X, tc, child.X, tc, child.X.length - tc); //[tc..size]
        return child;
    }
};
