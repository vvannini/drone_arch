/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Permutation;

import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iPerInitRandom extends oInitialization<Problem, cPermutation>{
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public void initialize(Problem mem, cPermutation ind) throws Exception {
        for(int i=0; i<ind.cromo.length; i++){
            ind.cromo[i] = mem.rnd.nextInt(ind.min(i), ind.max(i));
        }
    }
}
