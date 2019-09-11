/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;



/**
 *
 * @author marcio
 */
public abstract class Stop extends Approach{
    public abstract double progress() throws Exception;
    public abstract boolean end() throws Exception ;
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
}
