/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem.meta;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 */
public abstract class Best extends Approach{
    protected static boolean force_finish = false;
    public static void force_finish(boolean flag){
        Best.force_finish = flag;
    }
    
    public abstract void better(Problem prob, Solution sol) throws Exception;
    public abstract void flush(Problem prob) throws Exception;
    public abstract double time_now();
    public abstract double time_best();
    public abstract double time_after();
    public abstract void results(Problem prob, LinkerResults link) throws Exception;
    public abstract Solution ind() throws Exception;
    public abstract long id() throws Exception;
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public final boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public final void results(LinkerResults link) throws Exception {
    
    }
    
}
