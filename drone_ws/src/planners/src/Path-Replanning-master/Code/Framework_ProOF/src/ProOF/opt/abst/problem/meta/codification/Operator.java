package ProOF.opt.abst.problem.meta.codification;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;


/**
 *
 * @author Márcio
 */
public abstract class Operator extends Approach{
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
    @Override
    public final boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public String description() {
        return null;
    }
}
