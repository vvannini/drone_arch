/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP;

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
public class PPDCPFactory extends Problem<BestSol>{
    public final PPDCPInstance inst = new PPDCPInstance();
    
    @Override
    public String name() {
        return "PPDCP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(this.inst.T*2);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new PPDCPObjective();
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(PPDCPOperator.obj);
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
}
