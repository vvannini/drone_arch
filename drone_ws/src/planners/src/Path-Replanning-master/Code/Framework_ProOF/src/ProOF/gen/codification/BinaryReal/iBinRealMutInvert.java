/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.BinaryReal;

import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinRealMutInvert extends oMutation<Problem, cBinaryReal>{
    @Override
    public String name(){
        return "Invert";
    }
    @Override
    public void mutation(Problem mem, cBinaryReal ind) throws Exception {
        int i = mem.rnd.nextInt(ind.X.length);
        int j = mem.rnd.nextInt(ind.X[i].length);
        ind.X[i][j] = !ind.X[i][j];
    }
};
