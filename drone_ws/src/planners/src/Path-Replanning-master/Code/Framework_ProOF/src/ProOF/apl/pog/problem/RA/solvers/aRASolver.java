package ProOF.apl.pog.problem.RA.solvers;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.language.Approach;


/**
 *
 * @author andre
 */
public abstract class aRASolver extends Approach{
    //protected RAProblem prob;
    @Override
    public void services(LinkerApproaches link) throws Exception {
        //this.prob = link.need(RAProblem.class, prob);
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
    
    public abstract double solve(int Yit[][], double STt[]);
    public abstract void printer(StreamPrinter com) throws Exception;
}
