/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.language;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;

/**
 *
 * @author marcio
 */
public abstract class ApproachSingle extends ApproachEnd{
    @Override
    public final void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public final boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public final void load() throws Exception {
    
    }
    @Override
    public final void results(LinkerResults link) throws Exception {
        
    }
}
