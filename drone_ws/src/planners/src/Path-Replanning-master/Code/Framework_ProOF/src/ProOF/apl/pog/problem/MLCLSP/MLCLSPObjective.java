/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MLCLSP;

import ProOF.apl.pog.problem.TSP.*;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class MLCLSPObjective extends SingleObjective<MLCLSPProblem, MLCLSPCodification, MLCLSPObjective> {

    public MLCLSPObjective() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(MLCLSPProblem mem, MLCLSPCodification codif) throws Exception {
        double fitness = 0;
        
        set(fitness);
    }

    @Override
    public MLCLSPObjective build(MLCLSPProblem mem) throws Exception {
        return new MLCLSPObjective();
    }
}
