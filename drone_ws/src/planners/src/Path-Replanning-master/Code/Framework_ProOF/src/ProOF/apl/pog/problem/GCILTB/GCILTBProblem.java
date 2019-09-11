/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author Hossomi
 */
public class GCILTBProblem extends Problem<BestSol> {

    protected final GCILTBInstance inst = new GCILTBInstance();
    protected aGCILTBModel model;

    @Override
    public String name() {
        return "GCI Long Term B";
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        model = link.get(fGCILTBModel.obj, model);
        super.services(link);
        link.add(inst);
        link.add(GCILTBOperators.obj);
        
    }
    @Override
    public Codification build_codif() throws Exception {
        return new GCILTBCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new GCILTBObjective();
    }
}
