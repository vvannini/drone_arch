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
public class gInitialization{
    private final Problem mem;
    private final oInitialization inits[];
    public gInitialization(Problem mem, oInitialization ...inits) {
        this.mem = mem;
        this.inits = inits;
    }
    public void initialize(Solution ind) throws Exception {
        int index = mem.rnd.nextInt(inits.length);
        inits[index].initialize(mem, ind.codif());
    }
}
