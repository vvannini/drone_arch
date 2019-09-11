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
public class SingleDoubleParameter extends ApproachParameter{
    private final String name;
    private final double init;
    private final double min;
    private final double max;
    private final String description;
    public double value;

    public SingleDoubleParameter(String name, double init, double min, double max, String description) {
        this.name = name;
        this.init = init;
        this.min = min;
        this.max = max;
        this.description = description;
    }

    /*@Override
    public void print(PrintStream out) throws Exception {
        out.printf("%s = %g\n", name, value);
    }*/
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        value = link.Dbl(name, init, min, max, description);
    }
    
    

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
