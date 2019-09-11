/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MLCLSP;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author marcio
 */
public class MLCLSPProblem extends Problem<BestSol> {
    public final MLCLSPInstance inst = new MLCLSPInstance();
    @Override
    public String name() {
        return "MLCLSP-2";
    }
    @Override
    public Codification build_codif() throws Exception {
        return new MLCLSPCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new MLCLSPObjective();
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(MLCLSPOperator.obj);
    }
}
