/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.Exact;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 */
public abstract class ExactBest extends Approach{
    protected static boolean force_finish = false;
    public static void force_finish(boolean flag){
        ExactBest.force_finish = flag;
    }
    
    public abstract void better(aComb prob, ExactNode base) throws Exception;
    public abstract void flush(aComb prob) throws Exception;
    public abstract double time_now();
    public abstract double time_best();
    public abstract double time_after();
    public abstract void results(aComb prob, LinkerResults com) throws Exception;
    public abstract ExactNode ind() throws Exception;
    public abstract long id() throws Exception;
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        
    }
    @Override
    public final boolean validation(LinkerValidations win) throws Exception {
        return true;
    }
    @Override
    public final void results(LinkerResults com) throws Exception {
    
    }
    
}
