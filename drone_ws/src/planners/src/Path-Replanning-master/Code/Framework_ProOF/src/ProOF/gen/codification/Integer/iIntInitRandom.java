/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Integer;

import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iIntInitRandom extends oInitialization<Problem, cInteger>{
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public void initialize(Problem mem, cInteger ind) throws Exception {
        for(int i=0; i<ind.cromo.length; i++){
            ind.cromo[i] = mem.rnd.nextInt(ind.min(i), ind.max(i));
        }
    }
}
