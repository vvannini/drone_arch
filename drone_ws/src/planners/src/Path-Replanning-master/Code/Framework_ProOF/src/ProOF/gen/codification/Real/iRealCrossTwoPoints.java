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
public class iRealCrossTwoPoints extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "Two-points";
    }
    @Override
    public cReal crossover(Problem prob, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(prob);

        int points[] = prob.rnd.nextCutPoints(new int[2], child.X.length);

        boolean flag = true;
        int j = 0;
        for(int p : points){
            if(flag){
                System.arraycopy(ind1.X, j, child.X, j, p - j);
            }else{
                System.arraycopy(ind2.X, j, child.X, j, p - j);
            }
            j = p;
            flag = !flag;
        }
        if(flag){
            System.arraycopy(ind1.X, j, child.X, j, child.X.length - j);
        }else{
            System.arraycopy(ind2.X, j, child.X, j, child.X.length - j);
        }
        return child;
    }
};
