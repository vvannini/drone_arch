/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.apl.pog.gen.callback.cbReinitialize;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class GCIFactory extends Problem<BestSol> {
    protected final GCIInstance inst = new GCIInstance(); 
    public aGCI model;
    //public nPopulation call_back;
    @Override
    public String name() {
        return "GCI";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        model = link.get(fGCI.obj, model);
        //call_back = link.need(nPopulation.class, call_back);
        super.services(link);
        link.add(inst);
        link.add(GCIOperator.obj);
    }

    @Override
    public void start() throws Exception {
//        call_back.add(new cbReinitialize<GCICodification>() {
//            @Override
//            public void reinitialize(GCICodification ind, GCICodification best, int ID, int MaxID) throws Exception {
//                ind.te = best.te + (MaxID/2-ID);    //te = {+2,+1,+0,-1}
//                ind.te = Math.max(ind.te, 1);
//                ind.te = Math.max(ind.te, inst.T);
//            }
//        });
        if(inst.optimal != Double.NaN){
            add_gap("gap", inst.optimal);
        }
    }
    
    @Override
    public Codification build_codif() throws Exception {
        return new GCICodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new GCIObjective();
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
}
