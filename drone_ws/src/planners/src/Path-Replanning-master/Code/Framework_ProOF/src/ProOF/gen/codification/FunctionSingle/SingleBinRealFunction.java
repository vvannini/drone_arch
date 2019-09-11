/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionSingle;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ProOF.gen.codification.BinaryReal.cBinaryReal;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class SingleBinRealFunction extends SingleFunction{
    private int length;
    public SingleBinRealFunction(Factory single, Factory operator) {
        super(single, operator);
    }
    @Override
    public String name() {
        return "Single - BinReal";
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        length = link.Int("length", 16, 2, 1048576);
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cBinaryReal(func.size(), length);
    }
    @Override
    public SingleObjective build_obj() throws Exception {
        return new ObjBinReal(bound);
    }
    private class ObjBinReal extends SingleObjective<SingleRealFunction, cBinaryReal, SingleObjective> {
        public ObjBinReal(BoundDbl bound) throws Exception {
            super(bound);
        }
        @Override
        public void evaluate(SingleRealFunction prob, cBinaryReal codif) throws Exception {
            set(func.evaluate(codif.X()));
        }
        @Override
        public SingleObjective build(SingleRealFunction prob) throws Exception {
            return new ObjBinReal(bound);
        }
    }
}

