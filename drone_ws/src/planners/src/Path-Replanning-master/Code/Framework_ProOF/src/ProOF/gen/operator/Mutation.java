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
public final class Mutation extends Approach{
    public final static Mutation obj = new Mutation();
    
    private Problem prob;
    private oMutation mut[];
    
    private Mutation() {
        
    }
    @Override
    public String name() {
        return "Mutation";
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
        mut = link.needs(oMutation.class, new oMutation[1]);
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
    
    public void mutation(Solution ind) throws Exception {
        int index = prob.rnd.nextInt(mut.length);
        mut[index].mutation(prob, ind.codif());
    }
}
