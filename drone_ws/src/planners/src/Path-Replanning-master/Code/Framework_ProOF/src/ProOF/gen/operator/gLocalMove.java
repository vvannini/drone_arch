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
public class gLocalMove{
    private final Problem mem;
    private final oLocalMove moves[];
    public gLocalMove(Problem mem, oLocalMove ...moves) {
        this.mem = mem;
        this.moves = moves;
    }
    public Solution local_search(Solution ind) throws Exception {
        int index = mem.rnd.nextInt(moves.length);
        Solution sol = ind.clone(mem);
        moves[index].local_search(mem, sol.codif());
        return sol;
    }
}
