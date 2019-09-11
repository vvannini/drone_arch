/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionSingle;

import ProOF.com.language.Factory;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class SingleRealFunction extends SingleFunction{
    
    public SingleRealFunction(Factory single, Factory operator) {
        super(single, operator);
    }
    
    @Override
    public String name() {
        return "Single - Real";
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(func.size());
    }
    @Override
    public SingleObjective build_obj() throws Exception {
        return new ObjReal(bound);
    }
    private class ObjReal extends SingleObjective<SingleRealFunction, cReal, SingleObjective> {
        public ObjReal(BoundDbl bound) throws Exception {
            super(bound);
        }
        @Override
        public void evaluate(SingleRealFunction prob, cReal codif) throws Exception {
            set(func.evaluate(codif.X));
        }
        @Override
        public SingleObjective build(SingleRealFunction prob) throws Exception {
            return new ObjReal(bound);
        }
    }
}

