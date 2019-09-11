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
public class GenericParameter extends ApproachParameter{
    public final SingleValue values[];
    public final String name;
    public GenericParameter(String name, SingleValue ...values) {
        this.name = name;
        this.values = values;
    }

    /*@Override
    public void print(PrintStream out) throws Exception {
        for(SingleValue v : values){
            out.printf("%s\n", v);
        }
    }*/
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        for(SingleValue v : values){
            if(v instanceof SingleDouble){
                SingleDouble s = (SingleDouble) v;
                s.value = link.Dbl(s.name, s.init, s.min, s.max, s.description);
            }else if(v instanceof SingleLong){
                SingleLong s = (SingleLong) v;
                s.value = link.Long(s.name, s.init, s.min, s.max, s.description);
            }
        }
    }
    @Override
    public final boolean validation(LinkerValidations link) throws Exception {
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
