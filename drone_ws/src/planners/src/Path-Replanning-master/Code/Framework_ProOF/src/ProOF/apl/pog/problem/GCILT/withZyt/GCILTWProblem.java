/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.com.language.Approach;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Objective;

/**
 *
 * @author Hossomi
 */
public class GCILTWProblem extends Problem<BestSol> {

    protected final GCILTInstance inst = new GCILTInstance();
    protected aGCILTModel model;

    @Override
    public String name() {
        return "GCI-W Long Term";
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        model = link.get(fGCILTModel.obj, model);
        super.services(link); //To change body of generated methods, choose Tools | Templates.
        link.add(inst);
        link.add(GCILTOperators.obj);
    }
    @Override
    public Codification build_codif() throws Exception {
        return new GCILTCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new GCILTObjective();
    }
}
