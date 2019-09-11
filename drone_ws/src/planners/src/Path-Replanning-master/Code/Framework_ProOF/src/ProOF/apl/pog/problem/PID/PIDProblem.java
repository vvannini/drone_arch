/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PID;


import ProOF.apl.factorys.fRealOperator;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class PIDProblem extends Problem<BestSol> {
    public final PIDInstance inst = new PIDInstance();
    @Override
    public String name() {
        return "PID";
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(3);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new PIDObjective();
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(fRealOperator.obj);
    }
}
