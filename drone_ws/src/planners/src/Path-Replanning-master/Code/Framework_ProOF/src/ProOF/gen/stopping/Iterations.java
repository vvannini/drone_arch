/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.Communication;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Stream.StreamProgress;


/**
 *
 * @author marcio
 */
public final class Iterations extends Stop{
    private CountIteration iter;
    private StreamProgress com;
    private long max_iterations;
    
    @Override
    public String name() {
        return "Stop for iterations";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        com = Communication.mkProgress("iteration progress");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        iter = (CountIteration) link.need(CountIteration.class);
    }
    @Override
    public final void parameters(LinkerParameters link) throws Exception {
        max_iterations = link.Long("Iterations", 1000, 1, Long.MAX_VALUE, "the maximum iterations");
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double progress() throws Exception {
        double p = iter.value()*1.0/max_iterations;
        com.progress(p);
        return p;
    }  
    @Override
    public boolean end() throws Exception {
        progress();
        return iter.value() > max_iterations ;
    }
}
