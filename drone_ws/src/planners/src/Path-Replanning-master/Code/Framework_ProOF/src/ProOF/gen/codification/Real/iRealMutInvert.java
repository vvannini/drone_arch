/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iRealMutInvert extends oMutation<Problem, cReal>{
    @Override
    public String name(){
        return "Invert";
    }
    @Override
    public void mutation(Problem mem, cReal ind) throws Exception {
        int i = mem.rnd.nextInt(ind.X.length);
        ind.X[i] = 1-ind.X[i];
    }
};
