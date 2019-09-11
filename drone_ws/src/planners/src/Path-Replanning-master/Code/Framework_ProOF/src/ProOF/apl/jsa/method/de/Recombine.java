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
public final class Recombine extends Approach{
    public final static Recombine obj = new Recombine();
    
    private Problem prob;
    private oRecombine recom[];
    
    private Recombine() {
        
    }
    @Override
    public String name() {
        return "Recombine";
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
        recom = link.needs(oRecombine.class, new oRecombine[1]);
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
    
    public Solution recombine(Solution ind1, Solution ind2, int rateCross) throws Exception {
        int index = prob.rnd.nextInt(recom.length);
        Codification sol = recom[index].recombine(prob, ind1.codif(), ind2.codif(), rateCross);        
        return prob.build_sol(sol);
    }
}
