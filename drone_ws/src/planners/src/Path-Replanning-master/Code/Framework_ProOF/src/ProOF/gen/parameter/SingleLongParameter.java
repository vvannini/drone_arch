/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.parameter;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.ApproachParameter;

/**
 *
 * @author marcio
 */
public class SingleLongParameter extends ApproachParameter{
    private final String name;
    private final long init;
    private final long min;
    private final long max;
    private final String description;
    
    public long value;

    public SingleLongParameter(String name, long init, long min, long max, String description) {
        this.name = name;
        this.init = init;
        this.min = min;
        this.max = max;
        this.description = description;
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        value = link.Long(name, init, min, max, description);
    }
    
    /*@Override
    public void print(PrintStream out) throws Exception {
        out.printf("%s = %d\n", name, value);
    }*/

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }

    @Override
    public String name() {
        return name;
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
