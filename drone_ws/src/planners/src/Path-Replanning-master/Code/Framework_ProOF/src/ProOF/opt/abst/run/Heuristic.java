/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.run;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;

/**
 *
 * @author marcio
 */
public abstract class Heuristic extends Run{
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public abstract void services(LinkerApproaches link) throws Exception;
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
}