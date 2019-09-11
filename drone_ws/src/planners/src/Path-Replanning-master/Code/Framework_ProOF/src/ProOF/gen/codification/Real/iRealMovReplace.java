/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oLocalMove;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iRealMovReplace extends oLocalMove<Problem, cReal>{
    @Override
    public String name(){
        return "Replace";
    }
    @Override
    public void local_search(Problem mem, cReal ind) throws Exception {
        int i = mem.rnd.nextInt(ind.X.length);
        ind.X[i] = mem.rnd.nextDouble();
    }
};
