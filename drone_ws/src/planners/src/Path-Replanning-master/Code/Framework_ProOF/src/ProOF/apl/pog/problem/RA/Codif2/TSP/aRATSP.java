/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif2.TSP;

import ProOF.apl.pog.problem.RA.model.RAInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import java.util.ArrayList;

/**
 *
 * @author marcio
 */
public abstract class aRATSP extends Approach{
    protected RAInstance inst;
    
    public abstract ArrayList<Integer>[] solve(int Wit[][]) throws Exception;
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        this.inst = link.need(RAInstance.class, this.inst);
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
        return true;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        
    }

    
}
