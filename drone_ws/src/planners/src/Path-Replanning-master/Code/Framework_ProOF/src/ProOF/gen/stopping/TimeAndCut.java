/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerValidations;
import ProOF.opt.abst.problem.meta.Best;


/**
 *
 * @author marcio
 */
public final class TimeAndCut extends Stop{
    private Best best;
    
    private double max_time;
    private double cut_time;
    private double alpha;
    
    private long initial_time;
    
    @Override
    public String name() {
        return "Time & Cut";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        best = (Best) link.need(Best.class);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        max_time = link.Dbl("max_time", 60, 1e-3, 1e7, "the maximum time of executiom");
        cut_time = link.Dbl("cut_time", 30, 1e-3, 1e7, "cut the execution case not have improvement");
        alpha = link.Dbl("alpha", 1.0, 1e-6, 1e7, "the alpha parameter");
    }
    
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() {
        this.initial_time = System.currentTimeMillis();
    }    
    @Override
    public double progress() {
        return time()/max_time;
    }
    @Override
    public boolean end() {
        double time = time();
        return time > max_time || time > cut_time + best.time_best() * alpha;
    }
    public double time() {
        return (System.currentTimeMillis() - initial_time)/1000.0;
    }
}
