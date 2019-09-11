/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionMulti;

import ProOF.com.language.Factory;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.MultiObjective;

/**
 *
 * @author marcio
 */
public class MultiRealFunction extends MultiFunction {
    
    public MultiRealFunction(Factory realMulti, Factory realOperator) {
        super(realMulti, realOperator);
    }
    
    @Override
    public String name() {
        return "Multi - Real";
    }

    @Override
    public Codification build_codif() throws Exception {
        return new cReal(func.size());
    }

    @Override
    public MultiObjective build_obj() throws Exception {
        return new Obj(bounds);
    }
    private class Obj extends MultiObjective<Problem, cReal, MultiObjective> {
        public Obj(BoundDbl... bounds) throws Exception {
            super(bounds);
        }
        @Override
        public void evaluate(Problem prob, cReal codif) throws Exception {
            double penality = func.constrant(codif.X);
            for(int i=0; i<func.goals(); i++){
                set(i, func.evaluate(i, codif.X) + penality);
            }
        }
        @Override
        public MultiObjective build(Problem prob) throws Exception {
            return new Obj(bounds);
        }
    }

}

