/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Binary;

import ProOF.gen.operator.oLocalMove;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinMovInvert extends oLocalMove<Problem, cBinary>{
    @Override
    public String name(){
        return "Invert";
    }
    @Override
    public void local_search(Problem mem, cBinary ind) throws Exception {
        int i = mem.rnd.nextInt(ind.cromo.length);
        ind.cromo[i] = !ind.cromo[i];
    }
};
