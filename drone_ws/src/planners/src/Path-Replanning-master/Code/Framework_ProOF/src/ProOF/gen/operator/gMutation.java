/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.operator;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;

/**
 *
 * @author marcio
 */
public class gMutation{
    private final Problem mem;
    private final oMutation muts[];
    public gMutation(Problem mem, oMutation ...muts) {
        this.mem = mem;
        this.muts = muts; 
    }
    public void mutation(Solution ind) throws Exception {
        int index = mem.rnd.nextInt(muts.length);
        Solution sol = ind.clone(mem);
        muts[index].mutation(mem, sol.codif());
    }
}
