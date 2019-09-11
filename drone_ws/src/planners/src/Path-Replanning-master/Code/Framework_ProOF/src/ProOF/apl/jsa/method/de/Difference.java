/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.method.de;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class Difference extends Approach{
    public final static Difference obj = new Difference();
    
    private Problem prob;
    private oDifference diff[];
    
    private Difference() {
        
    }
    @Override
    public String name() {
        return "Difference";
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
        diff = link.needs(oDifference.class, new oDifference[1]);
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public void results(LinkerResults com) throws Exception {
        
    }
    
    public Solution difference(Solution ind1, Solution ind2) throws Exception {
        int index = prob.rnd.nextInt(diff.length);
        Codification sol = diff[index].difference(prob, ind1.codif(), ind2.codif());        
        return prob.build_sol(sol);
    }
}
