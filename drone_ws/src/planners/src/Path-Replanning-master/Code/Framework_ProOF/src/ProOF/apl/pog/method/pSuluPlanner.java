/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;

/**
 *
 * @author marcio
 */
public class pSuluPlanner extends Run{
    @Override
    public String name() {
        return "p-Sulu Planner";
    }

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
    public void load() throws Exception {
        
    }

    @Override
    public void start() throws Exception {
        
    }

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute() throws Exception {
        double incumbent = Integer.MAX_VALUE;
        
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
