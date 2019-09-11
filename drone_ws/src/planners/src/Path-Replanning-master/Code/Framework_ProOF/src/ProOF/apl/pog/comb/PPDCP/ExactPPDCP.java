/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.PPDCP;

import ProOF.apl.pog.method.Exact.ExactBest;
import ProOF.apl.pog.method.Exact.ExactBestSol;
import ProOF.apl.pog.method.Exact.ExactNode;
import ProOF.apl.pog.method.Exact.aComb;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerApproaches;

/**
 *
 * @author marcio
 */
public class ExactPPDCP extends aComb{
    protected PPDCPInstance inst = new PPDCPInstance();
    
    @Override
    public String name() {
        return "exact-PPDCP";
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(ExactPPDCPOperator.obj); 
    }
    
    @Override
    public ExactNode frist_node() throws Exception {
        return new ExactPPDCPStage(this, null, inst.X0());
    }
    @Override
    public ExactBest best() {
        return ExactBestSol.object();
    }
}
