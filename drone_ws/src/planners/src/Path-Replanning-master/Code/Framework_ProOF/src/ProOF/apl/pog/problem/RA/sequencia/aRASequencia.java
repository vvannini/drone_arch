package ProOF.apl.pog.problem.RA.sequencia;

import ProOF.apl.pog.problem.RA.model.RAInstance;
import ProOF.com.Linker.*;
import ProOF.com.language.Approach;
import java.util.ArrayList;


/**
 *
 * @author andre
 */
public abstract class aRASequencia extends Approach{
    protected RAInstance inst;
    public double STt[];
    @Override
    public void services(LinkerApproaches link) throws Exception {
        this.inst = link.need(RAInstance.class, inst);
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
    }

    @Override
    public void load() throws Exception {
        
    }

    @Override
    public void start() throws Exception {
        STt = new double[inst.T];
    }

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
    }
    
    
    
    public abstract double tempoSetup(ArrayList<Integer> Yt[]);

}
