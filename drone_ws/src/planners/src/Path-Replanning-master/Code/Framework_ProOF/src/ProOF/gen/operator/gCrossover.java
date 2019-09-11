/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.operator;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class gCrossover{
    private final Problem prob;
    private final oCrossover cross[];
    
    public gCrossover(Problem prob, oCrossover ...cross) {
        this.prob = prob;
        this.cross = cross;
    }
    public Solution crossover(Solution ind1, Solution ind2) throws Exception {
        int index = prob.rnd.nextInt(cross.length);
        
        Codification sol = ind1.compareTo(ind2)<=0 ? 
                cross[index].crossover(prob, ind1.codif(), ind2.codif()) : 
                cross[index].crossover(prob, ind2.codif(), ind1.codif()) ;
        
        return prob.build_sol(sol);
    }
}
