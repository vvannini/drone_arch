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
public class iRealCrossGeo extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "Geometric";
    }
    @Override
    public cReal crossover(Problem mem, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(mem);
        for(int i=0; i<child.X.length; i++){
            child.X[i] = Math.sqrt(ind1.X[i]*ind2.X[i]);
        }
        return child;
    }
};
