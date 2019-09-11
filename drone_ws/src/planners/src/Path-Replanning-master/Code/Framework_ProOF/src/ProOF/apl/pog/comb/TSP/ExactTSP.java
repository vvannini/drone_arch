/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.TSP;

import ProOF.apl.pog.method.Exact.ExactBest;
import ProOF.apl.pog.method.Exact.ExactBestSol;
import ProOF.apl.pog.method.Exact.ExactNode;
import ProOF.apl.pog.method.Exact.aComb;
import ProOF.apl.pog.problem.TSP.TSPInstance;
import ProOF.com.Linker.LinkerApproaches;

/**
 *
 * @author marcio
 */
public class ExactTSP extends aComb{
    protected TSPInstance inst = new TSPInstance();
    
    @Override
    public String name() {
        return "exact-TSP";
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(ExactTSPOperator.obj); 
    }
    
    @Override
    public ExactNode frist_node() throws Exception {
        return new ExactTSPCity(this, null, 0);
    }
    @Override
    public ExactBest best() {
        return ExactBestSol.object();
    }
}
