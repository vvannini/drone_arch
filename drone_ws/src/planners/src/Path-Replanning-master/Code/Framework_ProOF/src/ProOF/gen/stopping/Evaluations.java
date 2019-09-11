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
import ProOF.gen.best.nEvaluations;

/**
 *
 * @author marcio
 */
public final class Evaluations extends Stop{
    public static final Evaluations obj = new Evaluations();
    
    private nEvaluations eval;
    private StreamProgress com;
    private long max_evaluations;
    
    @Override
    public String name() {
        return "Evaluations";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        eval = (nEvaluations) link.need(nEvaluations.class);
    }
    @Override
    public void load() throws Exception {
        com = Communication.mkProgress("evaluation progress");
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        max_evaluations = link.Long("Evaluations", 1000, 1, Long.MAX_VALUE, "the maximum evaluations");
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    
    @Override
    public double progress() throws Exception {
        double p = eval.value()*1.0/max_evaluations;
        com.progress(p);
        return p;
    }  
    @Override
    public boolean end() throws Exception {
        progress();
        return eval.value() > max_evaluations;
    }
}
