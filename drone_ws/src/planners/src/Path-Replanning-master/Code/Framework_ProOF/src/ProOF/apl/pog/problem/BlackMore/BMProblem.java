/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;

import ProOF.apl.pog.problem.BlackMore.LP2D.LP2DControl;
import ProOF.apl.pog.problem.BlackMore.LP2D.LP2DServices;
import ProOF.apl.pog.problem.BlackMore.LP2D.LP2DState;
import ProOF.apl.pog.problem.PPDCP.*;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerResults;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author marcio
 */
public class BMProblem extends Problem<BestSol>{
    public final PPDCPInstance inst = new PPDCPInstance();
    public LP2DServices serv;
    public BMAddObj obj;
    
    @Override
    public String name() {
        return "BlackMore";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new BMCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new BMObjective();
    }

    @Override
    public void start() throws Exception {
        super.start();
        obj = serv.selectOBJ(this);
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(BMOperator.obj);
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
}
