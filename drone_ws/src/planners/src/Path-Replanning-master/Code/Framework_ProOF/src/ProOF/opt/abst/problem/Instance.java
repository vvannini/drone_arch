/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.opt.abst.problem;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;

/**
 *
 * @author Márcio
 */
public abstract class Instance extends Approach{
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public final void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public final void start() throws Exception {
        
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
}
