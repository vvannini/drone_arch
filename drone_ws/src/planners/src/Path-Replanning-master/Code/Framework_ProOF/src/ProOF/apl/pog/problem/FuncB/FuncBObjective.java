/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.FuncB;

import ProOF.opt.abst.problem.meta.objective.SingleObjective;
import ProOF.gen.codification.Real.cReal;

/**
 *
 * @author marcio
 */
public class FuncBObjective extends SingleObjective<
    FuncBFactory, cReal, SingleObjective
> {
    public FuncBObjective() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(FuncBFactory mem, cReal codif) throws Exception {
        //Decoder
        double x = codif.X(0, mem.param.min_x(), mem.param.max_x());
        //Evaluate
        double fitness = (x * Math.sin(10*Math.PI*x)+1);
        set(fitness);
    }
    @Override
    public SingleObjective build(FuncBFactory mem) throws Exception {
        return new FuncBObjective();
    }
}
