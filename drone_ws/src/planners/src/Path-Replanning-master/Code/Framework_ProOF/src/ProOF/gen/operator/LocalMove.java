/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.operator;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;

/**
 *
 * @author marcio
 */
public final class LocalMove extends Approach{
    public final static LocalMove obj = new LocalMove();
    
    private Problem prob;
    private oLocalMove move[];
    
    private LocalMove() {
        
    }
    @Override
    public String name() {
        return "LocalMove";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        prob = link.need(Problem.class, prob);
        move = link.needs(oLocalMove.class, new oLocalMove[1]);
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
    public Solution local_search(Solution ind) throws Exception {
        int index = prob.rnd.nextInt(move.length);
        Solution sol = ind.clone(prob);
        move[index].local_search(prob, sol.codif());
        return sol;
    }
}
