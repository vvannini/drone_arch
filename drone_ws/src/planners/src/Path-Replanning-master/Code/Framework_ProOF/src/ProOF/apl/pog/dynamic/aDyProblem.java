package ProOF.apl.pog.dynamic;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */





import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.utilities.uRandom;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public abstract class aDyProblem<S extends DyState, Control> extends ProOF.com.language.Approach {
    public final uRandom rmd = new uRandom();
    
    public abstract int T() throws Exception;
    public abstract S init_state(S base) throws Exception;
    public abstract S last_states() throws Exception;
    public abstract double Cn(S x) throws Exception;
    public abstract double Ct(int t, S x, Control u) throws Exception;
    public abstract double Hn(S x) throws Exception;
    public abstract double Ht(int t, S x, Control u) throws Exception;
    
    
    public abstract Control control(S s) throws Exception;
    public abstract S back(S x, Control u) throws Exception;
    public abstract S next(S x, Control u) throws Exception;
    public abstract Control correction(Control control, S x, S r) throws Exception;
    public abstract void add_plot(LinkedList<S> sol, double ref, double cost);
    
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
    public void finish() throws Exception {
        
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }

    

    

}
