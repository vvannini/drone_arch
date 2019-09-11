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
public final class Integers extends Stop{
    private CountInteger integer;
    private StreamProgress com;
    private long max_integers;
    
    @Override
    public String name() {
        return "Int Solutions";
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
        integer = (CountInteger) link.need(CountInteger.class);
    }
    @Override
    public final void parameters(LinkerParameters link) throws Exception {
        max_integers = link.Long("Integer solutions", 1000, 1, Long.MAX_VALUE, "the maximum of integer solutions");
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public double progress() throws Exception {
        double p = integer.value()*1.0/max_integers;
        com.progress(p);
        return p;
    }  
    @Override
    public boolean end() throws Exception {
        progress();
        return integer.value() > max_integers ;
    }
}
