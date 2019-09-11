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
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class Crossover extends Approach{
    public final static Crossover obj = new Crossover();
    
    private Problem prob;
    private oCrossover cross[];
    
    private Crossover() {
        
    }
    @Override
    public String name() {
        return "Crossover";
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
        cross = link.needs(oCrossover.class, new oCrossover[1]);
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
    
    public Solution crossover(Solution ind1, Solution ind2) throws Exception {
        int index = prob.rnd.nextInt(cross.length);
        
        Codification sol = ind1.compareTo(ind2)<=0 ? 
                cross[index].crossover(prob, ind1.codif(), ind2.codif()) : 
                cross[index].crossover(prob, ind2.codif(), ind1.codif()) ;
        
        return prob.build_sol(sol);
    }
}
