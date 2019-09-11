/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.Communication;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Stream.StreamProgress;

/**
 *
 * @author marcio
 */
public final class Time extends Stop{
    private long initial_time;
    private StreamProgress com;
    private double time;
    @Override
    public String name() {
        return "Time"; //Stop for time"
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        com = Communication.mkProgress("time progress");
    }
    @Override
    public void start() throws Exception {
        this.initial_time = System.currentTimeMillis();
    }
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        time = link.Dbl("time", 60, 1e-3, 1e7, "the time of execution (seconds)");
    }
    
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double progress() throws Exception {
        double p = time()/time;
        com.progress(p);
        return p;
    }
    @Override
    public boolean end() throws Exception {
        return progress() > 1.0;
    }    
    public double time() {
        return (System.currentTimeMillis() - initial_time)/1000.0;
    }
    public double timeRemaining(){
        return time-time();
    }
}
