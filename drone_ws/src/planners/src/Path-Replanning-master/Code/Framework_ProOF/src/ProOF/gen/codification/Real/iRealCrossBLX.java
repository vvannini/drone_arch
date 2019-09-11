/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.com.Linker.LinkerParameters;
import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.utilities.uUtil;

/**
 *
 * @author marcio
 */
public class iRealCrossBLX extends oCrossover<Problem, cReal>{
    private double alpha;
    @Override
    public String name() {
        return "BLX-a";
    }

    @Override
    public void parameters(LinkerParameters win) throws Exception {
        alpha = win.Dbl("BLX-alpha", 1.0, 0.0, 2.0);
    }

    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        for(int i=0; i<child.X.length; i++){
            //beta pertence a uma distribuição uniforme de (-Alfa, Alfa+1)
            double beta = prob.rnd.nextDouble(-alpha, 1+alpha);
            //c = p1 + beta*(p2 - p1 )
            child.X[i] = ind1.X[i]+ beta * (ind2.X[i] - ind1.X[i]);
            child.X[i] = uUtil.bound(child.X[i], 0.0, 1.0);
        }
        return child;
    }
};