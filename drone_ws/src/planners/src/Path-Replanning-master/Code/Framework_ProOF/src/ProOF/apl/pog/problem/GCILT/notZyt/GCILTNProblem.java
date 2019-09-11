/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.notZyt;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author Hossomi
 */
public class GCILTNProblem extends Problem<BestSol> {

    protected final GCILTInstance inst = new GCILTInstance();
    protected aGCILTModel model;
    

    @Override
    public String name() {
        return "GCI-N Long Term";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BestSol best() {
        return BestSol.object();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        model = link.get(fGCILTModel.obj, model);
        super.services(link); //To change body of generated methods, choose Tools | Templates.l
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
