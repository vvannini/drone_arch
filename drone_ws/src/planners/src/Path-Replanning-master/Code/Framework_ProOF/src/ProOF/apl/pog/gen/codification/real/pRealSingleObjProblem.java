/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.codification.real;
import ProOF.apl.factorys.fRealOperator;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public final class pRealSingleObjProblem extends Problem<BestSol>{
    
    public final aRealSingleObj function;
    
    public pRealSingleObjProblem(aRealSingleObj function) {
        this.function = function;
    }
    @Override
    public String name() {
        return function.name();
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(function.size());
    }
    @Override
    public Objective build_obj() throws Exception {
        return new SingleObjective() {
            @Override
            public void evaluate(Problem mem, Codification cod) throws Exception {
                cReal codif = (cReal) cod;
                set(function.F(codif.X));
            }
            @Override
            public SingleObjective build(Problem mem) throws Exception {
                return this.getClass().newInstance();
            }
        };
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BestSol best(){
        return BestSol.object();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(fRealOperator.obj);
    }
}
