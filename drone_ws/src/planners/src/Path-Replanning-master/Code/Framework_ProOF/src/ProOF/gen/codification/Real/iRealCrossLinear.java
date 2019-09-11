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
public class iRealCrossLinear extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "Linear";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);
        int type = prob.rnd.nextInt(3);
        switch(type){
            case 0 :
                for(int i=0; i<child.X.length; i++){
                    child.X[i] = (ind1.X[i] + ind2.X[i])/2;
                }
                break;
            case 1 :
                for(int i=0; i<child.X.length; i++){
                    child.X[i] = 1.5*ind1.X[i] -0.5*ind2.X[i];
                    child.X[i] = uUtil.bound(child.X[i], 0.0, 1.0);
                }
                break;
            default:
                for(int i=0; i<child.X.length; i++){
                    child.X[i] = -0.5*ind1.X[i] + 1.5*ind2.X[i];
                    child.X[i] = uUtil.bound(child.X[i], 0.0, 1.0);
                }
                break;
        }
        return child;
    }
};
