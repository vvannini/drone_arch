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
public class iRealCrossHeur extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "Heuristic";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        for(int i=0; i<child.X.length; i++){
            double r = prob.rnd.nextDouble();
            child.X[i] = ind1.X[i] + r * (ind1.X[i] - ind2.X[i]);
            child.X[i] = uUtil.bound(child.X[i], 0.0, 1.0);
        }
        return child;
    }
};
