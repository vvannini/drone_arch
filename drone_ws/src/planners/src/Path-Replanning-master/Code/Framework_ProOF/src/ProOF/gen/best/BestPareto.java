/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.best;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;

/**
 *
 * @author marcio
 */
public class BestPareto extends Best{

    @Override
    public String name() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Solution ind() throws Exception {
        return null;
    }
    
    
    @Override
    public long id() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void better(Problem prob, Solution sol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void flush(Problem prob) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double time_now() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double time_best() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double time_after() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void results(Problem mem, LinkerResults link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
