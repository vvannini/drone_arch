/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Binary;

import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinInitRandom extends oInitialization<Problem, cBinary>{
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public void initialize(Problem mem, cBinary ind) throws Exception {
        for(int i=0; i<ind.cromo.length; i++){
            ind.cromo[i] = mem.rnd.nextBoolean();
        }
    }
}
