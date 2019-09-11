package ProOF.opt.abst.problem.meta.codification;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.Problem;
import java.io.Serializable;

/**
 *
 * @author Márcio
 */
public abstract class Codification<Prob extends Problem, Codif extends Codification> implements Serializable{

    public void printer(Prob prob, StreamPrinter stream) throws Exception{
        
    }
    
    public void resulter(Prob prob, LinkerResults link) throws Exception{
        
    }
    /**
     * 
     * @param prob
     * @param source
     * @throws Exception 
     */
    public abstract void copy(Prob prob, Codif source) throws Exception;

    /**
     * 
     * @param prob
     * @return
     * @throws Exception 
     */
    public abstract Codif build(Prob prob) throws Exception;
    
    /**
     * 
     * @param prob
     * @return
     * @throws Exception 
     */
    public final Codif clone(Prob prob) throws Exception{
        Codif sol = build(prob);
        sol.copy(prob, this);
        return sol;
    }
}
